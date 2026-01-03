package com.simplemagnify.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.util.concurrent.Executors

class CameraManager(private val context: Context) {
    var isCameraReady by mutableStateOf(false)
        private set

    var isFlashlightOn by mutableStateOf(false)
        private set

    var zoomLevel by mutableStateOf(1.0f)
        private set

    var capturedBitmap by mutableStateOf<Bitmap?>(null)
        private set

    var isCapturing by mutableStateOf(false)
        private set

    var maxZoom by mutableStateOf(3.0f)
        private set

    private var camera: Camera? = null
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null

    private val cameraExecutor = Executors.newSingleThreadExecutor()

    fun startCamera(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()

                // Preview use case
                preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                // High-resolution image capture with JPEG output
                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                    .setTargetRotation(previewView.display.rotation)
                    .build()

                // Select back camera
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                // Unbind previous use cases
                cameraProvider?.unbindAll()

                // Bind use cases to camera
                camera = cameraProvider?.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )

                // Get max zoom - limit to 3x for preview (optical range)
                camera?.cameraInfo?.zoomState?.value?.let { zoomState ->
                    maxZoom = minOf(zoomState.maxZoomRatio, 3.0f)
                }

                // Enable auto-focus
                camera?.cameraControl?.cancelFocusAndMetering()

                isCameraReady = true

            } catch (e: Exception) {
                Log.e("CameraManager", "Camera binding failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun setZoom(level: Float) {
        val clampedZoom = level.coerceIn(1.0f, maxZoom)
        camera?.cameraControl?.setZoomRatio(clampedZoom)
        zoomLevel = clampedZoom
    }

    fun toggleFlashlight() {
        val newState = !isFlashlightOn
        camera?.cameraControl?.enableTorch(newState)
        isFlashlightOn = newState
    }

    fun setFlashlight(on: Boolean) {
        camera?.cameraControl?.enableTorch(on)
        isFlashlightOn = on
    }

    fun focusOnPoint(x: Float, y: Float, previewView: PreviewView) {
        val factory = previewView.meteringPointFactory
        val point = factory.createPoint(x, y)
        val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
            .setAutoCancelDuration(2, java.util.concurrent.TimeUnit.SECONDS)
            .build()
        camera?.cameraControl?.startFocusAndMetering(action)
    }

    fun capturePhoto(onCaptured: () -> Unit = {}) {
        if (isCapturing) return

        val capture = imageCapture ?: return

        isCapturing = true

        // Create temp file for the image
        val photoFile = File(context.cacheDir, "captured_image_${System.currentTimeMillis()}.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        capture.takePicture(
            outputOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val bitmap = loadBitmapFromFile(photoFile)

                    // Delete temp file after loading
                    photoFile.delete()

                    ContextCompat.getMainExecutor(context).execute {
                        capturedBitmap = bitmap
                        isCapturing = false
                        if (bitmap != null) {
                            onCaptured()
                        } else {
                            Log.e("CameraManager", "Failed to load bitmap from file")
                        }
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraManager", "Photo capture failed", exception)
                    photoFile.delete()
                    ContextCompat.getMainExecutor(context).execute {
                        isCapturing = false
                    }
                }
            }
        )
    }

    private fun loadBitmapFromFile(file: File): Bitmap? {
        return try {
            // Get the rotation from EXIF data
            val exif = ExifInterface(file.absolutePath)
            val rotation = when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }

            // Decode the bitmap
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)

            // Rotate if needed
            if (rotation != 0f && bitmap != null) {
                val matrix = Matrix()
                matrix.postRotate(rotation)
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            } else {
                bitmap
            }
        } catch (e: Exception) {
            Log.e("CameraManager", "Error loading bitmap from file", e)
            null
        }
    }

    fun clearCapturedFrame() {
        capturedBitmap = null
    }

    fun stopCamera() {
        setFlashlight(false)
        cameraProvider?.unbindAll()
        isCameraReady = false
    }

    fun shutdown() {
        cameraExecutor.shutdown()
    }
}
