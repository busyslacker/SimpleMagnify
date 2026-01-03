package com.simplemagnify.camera

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
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

    var maxZoom by mutableStateOf(10.0f)
        private set

    private var camera: Camera? = null
    private var preview: Preview? = null
    private var imageAnalysis: ImageAnalysis? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var lastFrame: Bitmap? = null

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

                // Image analysis for frame capture
                imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                            // Convert to bitmap for freeze functionality
                            lastFrame = imageProxy.toBitmap()
                            imageProxy.close()
                        }
                    }

                // Select back camera
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                // Unbind previous use cases
                cameraProvider?.unbindAll()

                // Bind use cases to camera
                camera = cameraProvider?.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )

                // Get max zoom
                camera?.cameraInfo?.zoomState?.value?.let { zoomState ->
                    maxZoom = minOf(zoomState.maxZoomRatio, 10.0f)
                }

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

    fun captureCurrentFrame() {
        capturedBitmap = lastFrame
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

// Extension to convert ImageProxy to Bitmap
private fun ImageProxy.toBitmap(): Bitmap? {
    val buffer = planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)

    return android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        ?: run {
            // Fallback for YUV format
            val yuvImage = android.graphics.YuvImage(
                bytes,
                android.graphics.ImageFormat.NV21,
                width,
                height,
                null
            )
            val out = java.io.ByteArrayOutputStream()
            yuvImage.compressToJpeg(android.graphics.Rect(0, 0, width, height), 90, out)
            val imageBytes = out.toByteArray()
            android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        }
}
