package com.simplemagnify.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.simplemagnify.camera.CameraManager
import com.simplemagnify.components.BigButton
import com.simplemagnify.components.BigSlider
import com.simplemagnify.models.AppSettings
import com.simplemagnify.models.ButtonPosition
import com.simplemagnify.utils.Constants

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    settings: AppSettings,
    onNavigateToSettings: () -> Unit,
    onNavigateToFrozenImage: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraManager = remember { CameraManager(context) }
    var zoomLevel by remember { mutableStateOf(settings.defaultZoom) }

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraManager.stopCamera()
            cameraManager.shutdown()
        }
    }

    // Apply light on start setting
    LaunchedEffect(cameraManager.isCameraReady) {
        if (cameraManager.isCameraReady && settings.lightOnStart) {
            cameraManager.setFlashlight(true)
        }
    }

    // Update zoom when slider changes
    LaunchedEffect(zoomLevel) {
        cameraManager.setZoom(zoomLevel)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(settings.backgroundColor)
    ) {
        // Header
        CameraHeader(
            settings = settings,
            onSettingsClick = onNavigateToSettings
        )

        // Camera Preview or Permission Request
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when {
                cameraPermissionState.status.isGranted -> {
                    CameraPreview(
                        cameraManager = cameraManager,
                        lifecycleOwner = lifecycleOwner,
                        onZoomChange = { delta ->
                            val newZoom = (zoomLevel * delta).coerceIn(Constants.Zoom.MIN, Constants.Zoom.MAX)
                            zoomLevel = newZoom
                        }
                    )
                }
                cameraPermissionState.status.shouldShowRationale -> {
                    PermissionRationale(
                        settings = settings,
                        onRequestPermission = { cameraPermissionState.launchPermissionRequest() }
                    )
                }
                else -> {
                    PermissionDenied(
                        settings = settings,
                        onOpenSettings = {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }

        // Controls
        CameraControls(
            settings = settings,
            zoomLevel = zoomLevel,
            isFlashlightOn = cameraManager.isFlashlightOn,
            onZoomChange = { zoomLevel = it },
            onLightToggle = { cameraManager.toggleFlashlight() },
            isCapturing = cameraManager.isCapturing,
            onFreeze = {
                cameraManager.capturePhoto {
                    // Copy bitmap to shared holder for navigation
                    CapturedImageHolder.bitmap = cameraManager.capturedBitmap
                    onNavigateToFrozenImage()
                }
            }
        )
    }
}

@Composable
private fun CameraHeader(
    settings: AppSettings,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Constants.Dimensions.headerHeight)
            .padding(horizontal = Constants.Dimensions.screenPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = Constants.Strings.APP_NAME,
            fontSize = Constants.FontSizes.header,
            fontWeight = FontWeight.SemiBold,
            color = settings.textColor
        )

        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier.size(Constants.Dimensions.minTapTarget)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = settings.textColor,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun CameraPreview(
    cameraManager: CameraManager,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    onZoomChange: (Float) -> Unit
) {
    var previewView by remember { mutableStateOf<PreviewView?>(null) }

    LaunchedEffect(previewView) {
        previewView?.let {
            cameraManager.startCamera(lifecycleOwner, it)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color.Black)
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, _ ->
                    onZoomChange(zoom)
                }
            }
    ) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).also {
                    it.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    previewView = it
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (!cameraManager.isCameraReady) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = Constants.Colors.buttonPrimary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Starting camera...",
                        color = androidx.compose.ui.graphics.Color.White,
                        fontSize = Constants.FontSizes.label
                    )
                }
            }
        }
    }
}

@Composable
private fun CameraControls(
    settings: AppSettings,
    zoomLevel: Float,
    isFlashlightOn: Boolean,
    isCapturing: Boolean,
    onZoomChange: (Float) -> Unit,
    onLightToggle: () -> Unit,
    onFreeze: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(settings.backgroundColor)
            .padding(Constants.Dimensions.screenPadding)
    ) {
        // Zoom Slider
        BigSlider(
            value = zoomLevel,
            onValueChange = onZoomChange,
            settings = settings
        )

        Spacer(modifier = Modifier.height(Constants.Dimensions.buttonSpacing))

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Constants.Dimensions.buttonSpacing)
        ) {
            if (settings.buttonPosition == ButtonPosition.LEFT) {
                Box(modifier = Modifier.weight(1f)) {
                    BigButton(
                        title = Constants.Strings.FREEZE,
                        icon = Icons.Default.PhotoCamera,
                        settings = settings,
                        enabled = !isCapturing,
                        onClick = onFreeze
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    BigButton(
                        title = Constants.Strings.LIGHT,
                        icon = if (isFlashlightOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                        settings = settings,
                        isActive = isFlashlightOn,
                        onClick = onLightToggle
                    )
                }
            } else {
                Box(modifier = Modifier.weight(1f)) {
                    BigButton(
                        title = Constants.Strings.LIGHT,
                        icon = if (isFlashlightOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                        settings = settings,
                        isActive = isFlashlightOn,
                        onClick = onLightToggle
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    BigButton(
                        title = Constants.Strings.FREEZE,
                        icon = Icons.Default.PhotoCamera,
                        settings = settings,
                        enabled = !isCapturing,
                        onClick = onFreeze
                    )
                }
            }
        }
    }
}

@Composable
private fun PermissionRationale(
    settings: AppSettings,
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(settings.backgroundColor)
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.PhotoCamera,
            contentDescription = null,
            tint = settings.textColor,
            modifier = Modifier.size(60.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Camera Access Required",
            fontSize = Constants.FontSizes.header,
            fontWeight = FontWeight.SemiBold,
            color = settings.textColor
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "SimpleMagnify needs camera access to magnify text and objects for easier reading.",
            fontSize = Constants.FontSizes.label,
            color = settings.textColor,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRequestPermission,
            colors = ButtonDefaults.buttonColors(containerColor = settings.buttonColor)
        ) {
            Text("Grant Permission", fontSize = Constants.FontSizes.button)
        }
    }
}

@Composable
private fun PermissionDenied(
    settings: AppSettings,
    onOpenSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(settings.backgroundColor)
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.PhotoCamera,
            contentDescription = null,
            tint = settings.textColor,
            modifier = Modifier.size(60.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Camera Access Required",
            fontSize = Constants.FontSizes.header,
            fontWeight = FontWeight.SemiBold,
            color = settings.textColor
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Please enable camera access in Settings to use SimpleMagnify.",
            fontSize = Constants.FontSizes.label,
            color = settings.textColor,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onOpenSettings,
            colors = ButtonDefaults.buttonColors(containerColor = settings.buttonColor)
        ) {
            Text("Open Settings", fontSize = Constants.FontSizes.button)
        }
    }
}
