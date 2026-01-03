import AVFoundation
import SwiftUI
import Combine

class CameraManager: NSObject, ObservableObject {
    @Published var isCameraReady = false
    @Published var isFlashlightOn = false
    @Published var zoomLevel: CGFloat = 1.0
    @Published var capturedImage: UIImage?
    @Published var permissionDenied = false
    @Published var isCapturing = false

    let session = AVCaptureSession()
    private var device: AVCaptureDevice?
    private var photoOutput = AVCapturePhotoOutput()

    var maxZoom: CGFloat {
        guard let device = device else { return 3.0 }
        // Limit to 3x for preview (optical range) - real zoom happens on captured image
        return min(device.activeFormat.videoMaxZoomFactor, 3.0)
    }

    override init() {
        super.init()
    }

    // MARK: - Permission
    func checkPermission() {
        switch AVCaptureDevice.authorizationStatus(for: .video) {
        case .authorized:
            setupCamera()
        case .notDetermined:
            AVCaptureDevice.requestAccess(for: .video) { [weak self] granted in
                DispatchQueue.main.async {
                    if granted {
                        self?.setupCamera()
                    } else {
                        self?.permissionDenied = true
                    }
                }
            }
        case .denied, .restricted:
            DispatchQueue.main.async {
                self.permissionDenied = true
            }
        @unknown default:
            break
        }
    }

    // MARK: - Camera Setup
    private func setupCamera() {
        session.beginConfiguration()
        session.sessionPreset = .photo

        // Try to get the best camera for macro/close-up (iPhone 13 Pro+ have macro mode)
        // Fall back to standard wide angle camera
        let videoDevice: AVCaptureDevice? = {
            // First try the default back camera (handles macro automatically on newer devices)
            if let device = AVCaptureDevice.default(.builtInWideAngleCamera, for: .video, position: .back) {
                return device
            }
            return AVCaptureDevice.default(for: .video)
        }()

        guard let videoDevice = videoDevice else {
            print("No camera available")
            return
        }

        self.device = videoDevice

        do {
            // Configure auto-focus and other settings
            try videoDevice.lockForConfiguration()

            // Enable continuous auto-focus
            if videoDevice.isFocusModeSupported(.continuousAutoFocus) {
                videoDevice.focusMode = .continuousAutoFocus
            }

            // Enable auto-focus range restriction for near objects (better for reading)
            if videoDevice.isAutoFocusRangeRestrictionSupported {
                videoDevice.autoFocusRangeRestriction = .near
            }

            // Enable continuous auto-exposure
            if videoDevice.isExposureModeSupported(.continuousAutoExposure) {
                videoDevice.exposureMode = .continuousAutoExposure
            }

            // Enable optical image stabilization if available
            if videoDevice.isLowLightBoostSupported {
                videoDevice.automaticallyEnablesLowLightBoostWhenAvailable = true
            }

            videoDevice.unlockForConfiguration()

            let videoInput = try AVCaptureDeviceInput(device: videoDevice)

            if session.canAddInput(videoInput) {
                session.addInput(videoInput)
            }

            // Add photo output for high-resolution capture
            if session.canAddOutput(photoOutput) {
                session.addOutput(photoOutput)
            }

            // Configure photo output after it's connected
            photoOutput.isHighResolutionCaptureEnabled = true
            if #available(iOS 16.0, *) {
                if let maxDimensions = videoDevice.activeFormat.supportedMaxPhotoDimensions.first {
                    photoOutput.maxPhotoDimensions = maxDimensions
                }
            }

            session.commitConfiguration()

            DispatchQueue.global(qos: .userInitiated).async { [weak self] in
                self?.session.startRunning()
                DispatchQueue.main.async {
                    self?.isCameraReady = true
                }
            }
        } catch {
            print("Error setting up camera: \(error)")
        }
    }

    // MARK: - Tap to Focus
    func focus(at point: CGPoint) {
        guard let device = device else { return }

        do {
            try device.lockForConfiguration()

            if device.isFocusPointOfInterestSupported {
                device.focusPointOfInterest = point
                device.focusMode = .autoFocus
            }

            if device.isExposurePointOfInterestSupported {
                device.exposurePointOfInterest = point
                device.exposureMode = .autoExpose
            }

            device.unlockForConfiguration()

            // Return to continuous auto-focus after a delay
            DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) { [weak self] in
                self?.resetToContinuousFocus()
            }
        } catch {
            print("Error focusing: \(error)")
        }
    }

    private func resetToContinuousFocus() {
        guard let device = device else { return }

        do {
            try device.lockForConfiguration()

            if device.isFocusModeSupported(.continuousAutoFocus) {
                device.focusMode = .continuousAutoFocus
            }

            if device.isExposureModeSupported(.continuousAutoExposure) {
                device.exposureMode = .continuousAutoExposure
            }

            device.unlockForConfiguration()
        } catch {
            print("Error resetting focus: \(error)")
        }
    }

    // MARK: - Zoom
    func setZoom(_ level: CGFloat) {
        guard let device = device else { return }

        let clampedZoom = max(1.0, min(level, maxZoom))

        do {
            try device.lockForConfiguration()
            device.videoZoomFactor = clampedZoom
            device.unlockForConfiguration()

            DispatchQueue.main.async {
                self.zoomLevel = clampedZoom
            }
        } catch {
            print("Error setting zoom: \(error)")
        }
    }

    // MARK: - Flashlight
    func toggleFlashlight() {
        guard let device = device, device.hasTorch else { return }

        do {
            try device.lockForConfiguration()

            if device.torchMode == .on {
                device.torchMode = .off
                isFlashlightOn = false
            } else {
                try device.setTorchModeOn(level: 1.0)
                isFlashlightOn = true
            }

            device.unlockForConfiguration()
        } catch {
            print("Error toggling flashlight: \(error)")
        }
    }

    func setFlashlight(_ on: Bool) {
        guard let device = device, device.hasTorch else { return }

        do {
            try device.lockForConfiguration()

            if on {
                try device.setTorchModeOn(level: 1.0)
            } else {
                device.torchMode = .off
            }
            isFlashlightOn = on

            device.unlockForConfiguration()
        } catch {
            print("Error setting flashlight: \(error)")
        }
    }

    // MARK: - Capture High-Resolution Photo
    func capturePhoto() {
        guard !isCapturing else { return }

        isCapturing = true

        var settings = AVCapturePhotoSettings()

        // Use HEIF if available for better quality, otherwise JPEG
        if photoOutput.availablePhotoCodecTypes.contains(.hevc) {
            settings = AVCapturePhotoSettings(format: [AVVideoCodecKey: AVVideoCodecType.hevc])
        }

        // Enable high resolution capture
        settings.isHighResolutionPhotoEnabled = true

        // Enable flash if flashlight is on
        if isFlashlightOn, let device = device, device.hasFlash {
            settings.flashMode = .on
        }

        photoOutput.capturePhoto(with: settings, delegate: self)
    }

    // MARK: - Cleanup
    func stopSession() {
        if session.isRunning {
            session.stopRunning()
        }
        setFlashlight(false)
    }
}

// MARK: - Photo Capture Delegate
extension CameraManager: AVCapturePhotoCaptureDelegate {
    func photoOutput(_ output: AVCapturePhotoOutput, didFinishProcessingPhoto photo: AVCapturePhoto, error: Error?) {
        isCapturing = false

        if let error = error {
            print("Error capturing photo: \(error)")
            return
        }

        guard let imageData = photo.fileDataRepresentation(),
              let image = UIImage(data: imageData) else {
            print("Could not get image data")
            return
        }

        DispatchQueue.main.async {
            self.capturedImage = image
        }
    }
}
