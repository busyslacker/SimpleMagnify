import SwiftUI

struct CameraView: View {
    @EnvironmentObject var settings: AppSettings
    @StateObject private var cameraManager = CameraManager()
    @State private var zoomLevel: CGFloat = 2.0
    @State private var showSettings = false
    @State private var showFrozenImage = false

    var body: some View {
        ZStack {
            // Background
            settings.backgroundColor.ignoresSafeArea()

            VStack(spacing: 0) {
                // Header
                headerView

                // Camera Preview
                cameraPreview

                // Controls
                controlsView
            }
        }
        .onAppear {
            zoomLevel = settings.defaultZoom
            cameraManager.checkPermission()

            // Apply initial settings
            if settings.lightOnStart {
                DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
                    cameraManager.setFlashlight(true)
                }
            }
        }
        .onDisappear {
            cameraManager.stopSession()
        }
        .onChange(of: zoomLevel) { newValue in
            cameraManager.setZoom(newValue)
        }
        .onChange(of: cameraManager.capturedImage) { newImage in
            if newImage != nil {
                showFrozenImage = true
            }
        }
        .sheet(isPresented: $showSettings) {
            SettingsView()
        }
        .fullScreenCover(isPresented: $showFrozenImage, onDismiss: {
            // Clear the captured image when dismissing
            cameraManager.capturedImage = nil
        }) {
            if let image = cameraManager.capturedImage {
                FrozenImageView(image: image, isPresented: $showFrozenImage)
            }
        }
    }

    // MARK: - Header
    private var headerView: some View {
        HStack {
            Text(Constants.Strings.appName)
                .font(Constants.Fonts.header)
                .foregroundColor(settings.textColor)

            Spacer()

            Button(action: {
                let impactFeedback = UIImpactFeedbackGenerator(style: .light)
                impactFeedback.impactOccurred()
                showSettings = true
            }) {
                Image(systemName: "gearshape.fill")
                    .font(.system(size: 28))
                    .foregroundColor(settings.textColor)
                    .frame(width: Constants.Dimensions.minTapTarget,
                           height: Constants.Dimensions.minTapTarget)
            }
            .accessibilityLabel("Settings")
        }
        .padding(.horizontal, Constants.Dimensions.screenPadding)
        .frame(height: Constants.Dimensions.headerHeight)
    }

    // MARK: - Camera Preview
    private var cameraPreview: some View {
        GeometryReader { geometry in
            ZStack {
                if cameraManager.permissionDenied {
                    permissionDeniedView
                } else if cameraManager.isCameraReady {
                    CameraPreviewView(session: cameraManager.session)
                        .gesture(
                            MagnificationGesture()
                                .onChanged { value in
                                    let delta = value - 1
                                    let newZoom = zoomLevel + delta
                                    zoomLevel = max(Constants.Zoom.min, min(newZoom, Constants.Zoom.max))
                                }
                        )
                        .onTapGesture { location in
                            // Convert tap location to camera coordinates (0-1 range)
                            let focusPoint = CGPoint(
                                x: location.y / geometry.size.height,
                                y: 1 - (location.x / geometry.size.width)
                            )
                            cameraManager.focus(at: focusPoint)

                            // Haptic feedback
                            let impactFeedback = UIImpactFeedbackGenerator(style: .light)
                            impactFeedback.impactOccurred()
                        }
                } else {
                    loadingView
                }
            }
            .frame(width: geometry.size.width, height: geometry.size.height)
            .clipped()
        }
    }

    private var loadingView: some View {
        VStack(spacing: 16) {
            ProgressView()
                .scaleEffect(2)
                .tint(settings.buttonColor)
            Text("Starting camera...")
                .font(Constants.Fonts.label)
                .foregroundColor(settings.textColor)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color.black)
    }

    private var permissionDeniedView: some View {
        VStack(spacing: 20) {
            Image(systemName: "camera.fill")
                .font(.system(size: 60))
                .foregroundColor(settings.textColor)

            Text("Camera Access Required")
                .font(Constants.Fonts.header)
                .foregroundColor(settings.textColor)

            Text("Please enable camera access in Settings to use SimpleMagnify.")
                .font(Constants.Fonts.label)
                .foregroundColor(settings.textColor)
                .multilineTextAlignment(.center)
                .padding(.horizontal, 40)

            Button(action: {
                if let url = URL(string: UIApplication.openSettingsURLString) {
                    UIApplication.shared.open(url)
                }
            }) {
                Text("Open Settings")
                    .font(Constants.Fonts.button)
                    .foregroundColor(.white)
                    .frame(width: 200, height: 60)
                    .background(settings.buttonColor)
                    .cornerRadius(Constants.Dimensions.cornerRadius)
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(settings.backgroundColor)
    }

    // MARK: - Controls
    private var controlsView: some View {
        VStack(spacing: Constants.Dimensions.buttonSpacing) {
            // Zoom Slider (limited to optical range for sharp preview)
            BigSlider(
                value: $zoomLevel,
                range: Constants.Zoom.min...Constants.Zoom.max,
                minLabel: "1x",
                maxLabel: "3x"
            )
            .padding(.horizontal, Constants.Dimensions.screenPadding)
            .padding(.top, Constants.Dimensions.buttonSpacing)

            // Buttons
            buttonRow
                .padding(.horizontal, Constants.Dimensions.screenPadding)
                .padding(.bottom, Constants.Dimensions.screenPadding)
        }
        .background(settings.backgroundColor)
    }

    private var buttonRow: some View {
        HStack(spacing: Constants.Dimensions.buttonSpacing) {
            if settings.buttonPosition == .left {
                freezeButton
                lightButton
            } else {
                lightButton
                freezeButton
            }
        }
    }

    private var lightButton: some View {
        BigButton(
            title: Constants.Strings.light,
            icon: cameraManager.isFlashlightOn ? "flashlight.on.fill" : "flashlight.off.fill",
            isActive: cameraManager.isFlashlightOn
        ) {
            cameraManager.toggleFlashlight()
        }
    }

    private var freezeButton: some View {
        BigButton(
            title: Constants.Strings.freeze,
            icon: "camera.fill"
        ) {
            cameraManager.capturePhoto()
        }
        .disabled(cameraManager.isCapturing)
        .opacity(cameraManager.isCapturing ? 0.6 : 1.0)
    }
}

struct CameraView_Previews: PreviewProvider {
    static var previews: some View {
        CameraView()
            .environmentObject(AppSettings())
    }
}
