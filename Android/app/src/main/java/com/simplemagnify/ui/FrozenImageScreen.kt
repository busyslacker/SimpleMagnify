package com.simplemagnify.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simplemagnify.components.BigButton
import com.simplemagnify.models.AppSettings
import com.simplemagnify.ocr.TextRecognitionManager
import com.simplemagnify.speech.SpeechManager
import com.simplemagnify.utils.Constants

enum class ViewMode {
    IMAGE, TEXT
}

@Composable
fun FrozenImageScreen(
    bitmap: Bitmap?,
    settings: AppSettings,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var viewMode by remember { mutableStateOf(ViewMode.IMAGE) }
    var recognizedText by remember { mutableStateOf<String?>(null) }
    var isProcessingOCR by remember { mutableStateOf(false) }

    val speechManager = remember { SpeechManager(context) }

    // Perform OCR when screen appears
    LaunchedEffect(bitmap) {
        bitmap?.let {
            isProcessingOCR = true
            TextRecognitionManager.recognizeTextWithLines(it,
                onSuccess = { text ->
                    recognizedText = text
                    isProcessingOCR = false
                    if (!text.isNullOrBlank()) {
                        viewMode = ViewMode.TEXT
                    }
                },
                onError = {
                    isProcessingOCR = false
                }
            )
        }
    }

    // Cleanup speech when leaving
    DisposableEffect(Unit) {
        onDispose {
            speechManager.stop()
            speechManager.shutdown()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(settings.backgroundColor)
        ) {
            // Header
            FrozenImageHeader(
                settings = settings,
                viewMode = viewMode,
                recognizedText = recognizedText,
                onBack = {
                    speechManager.stop()
                    onBack()
                },
                onCopy = {
                    recognizedText?.let {
                        clipboardManager.setText(AnnotatedString(it))
                    }
                }
            )

            // Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (viewMode) {
                    ViewMode.IMAGE -> ImageContent(
                        bitmap = bitmap,
                        scale = scale,
                        offset = offset,
                        onScaleChange = { scale = it },
                        onOffsetChange = { offset = it }
                    )
                    ViewMode.TEXT -> TextContent(
                        text = recognizedText,
                        settings = settings
                    )
                }
            }

            // Controls
            ControlsSection(
                settings = settings,
                viewMode = viewMode,
                recognizedText = recognizedText,
                speechManager = speechManager,
                isProcessingOCR = isProcessingOCR,
                onViewModeChange = { viewMode = it },
                onBack = {
                    speechManager.stop()
                    onBack()
                }
            )
        }

        // Processing overlay
        if (isProcessingOCR) {
            ProcessingOverlay(settings = settings)
        }
    }
}

@Composable
private fun FrozenImageHeader(
    settings: AppSettings,
    viewMode: ViewMode,
    recognizedText: String?,
    onBack: () -> Unit,
    onCopy: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Constants.Dimensions.headerHeight)
            .padding(horizontal = Constants.Dimensions.screenPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Back button
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(Constants.Dimensions.minTapTarget)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back",
                    tint = settings.buttonColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Back",
                    fontSize = Constants.FontSizes.button,
                    fontWeight = FontWeight.Medium,
                    color = settings.buttonColor
                )
            }
        }

        // Copy button (only in text mode with text)
        if (viewMode == ViewMode.TEXT && !recognizedText.isNullOrBlank()) {
            IconButton(
                onClick = onCopy,
                modifier = Modifier.size(Constants.Dimensions.minTapTarget)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy text",
                    tint = settings.buttonColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun ImageContent(
    bitmap: Bitmap?,
    scale: Float,
    offset: Offset,
    onScaleChange: (Float) -> Unit,
    onOffsetChange: (Offset) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    val newScale = (scale * zoom).coerceIn(1f, 15f)
                    onScaleChange(newScale)
                    if (newScale > 1f) {
                        onOffsetChange(
                            Offset(
                                offset.x + pan.x,
                                offset.y + pan.y
                            )
                        )
                    }
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        if (scale > 1f) {
                            onScaleChange(1f)
                            onOffsetChange(Offset.Zero)
                        } else {
                            onScaleChange(3f)
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        bitmap?.let { bmp ->
            Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = "Frozen magnified image",
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
            )
        } ?: run {
            Text(
                text = "No image captured",
                color = Color.White,
                fontSize = Constants.FontSizes.label
            )
        }
    }
}

@Composable
private fun TextContent(
    text: String?,
    settings: AppSettings
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
    ) {
        if (!text.isNullOrBlank()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(Constants.Dimensions.screenPadding)
            ) {
                Text(
                    text = text,
                    color = settings.textColor,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 36.sp
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SearchOff,
                    contentDescription = null,
                    tint = settings.textColor.copy(alpha = 0.5f),
                    modifier = Modifier.size(60.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No text found",
                    color = settings.textColor.copy(alpha = 0.5f),
                    fontSize = Constants.FontSizes.header,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Try capturing an image with clearer text",
                    color = settings.textColor.copy(alpha = 0.5f),
                    fontSize = Constants.FontSizes.label,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ControlsSection(
    settings: AppSettings,
    viewMode: ViewMode,
    recognizedText: String?,
    speechManager: SpeechManager,
    isProcessingOCR: Boolean,
    onViewModeChange: (ViewMode) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(settings.backgroundColor)
            .padding(Constants.Dimensions.screenPadding),
        verticalArrangement = Arrangement.spacedBy(Constants.Dimensions.buttonSpacing)
    ) {
        // Mode toggle buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Constants.Dimensions.buttonSpacing)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                BigButton(
                    title = "Image",
                    icon = Icons.Default.Photo,
                    settings = settings,
                    isActive = viewMode == ViewMode.IMAGE,
                    onClick = { onViewModeChange(ViewMode.IMAGE) }
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                BigButton(
                    title = "Text",
                    icon = Icons.Default.Description,
                    settings = settings,
                    isActive = viewMode == ViewMode.TEXT,
                    enabled = !isProcessingOCR,
                    onClick = { onViewModeChange(ViewMode.TEXT) }
                )
            }
        }

        // Speech controls (only in text mode with text)
        if (viewMode == ViewMode.TEXT && !recognizedText.isNullOrBlank()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Constants.Dimensions.buttonSpacing)
            ) {
                // Play/Pause button
                Box(modifier = Modifier.weight(1f)) {
                    val (title, icon) = when {
                        speechManager.isSpeaking -> "Pause" to Icons.Default.Pause
                        speechManager.isPaused -> "Resume" to Icons.Default.PlayArrow
                        else -> "Read" to Icons.Default.VolumeUp
                    }
                    BigButton(
                        title = title,
                        icon = icon,
                        settings = settings,
                        isActive = speechManager.isSpeaking,
                        onClick = { speechManager.togglePlayPause(recognizedText) }
                    )
                }

                // Stop button (only when speaking or paused)
                if (speechManager.isSpeaking || speechManager.isPaused) {
                    Box(modifier = Modifier.weight(1f)) {
                        BigButton(
                            title = "Stop",
                            icon = Icons.Default.Stop,
                            settings = settings,
                            onClick = { speechManager.stop() }
                        )
                    }
                }
            }
        }

        // Back to camera button
        BigButton(
            title = Constants.Strings.BACK_TO_CAMERA,
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            settings = settings,
            onClick = onBack
        )
    }
}

@Composable
private fun ProcessingOverlay(settings: AppSettings) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = settings.backgroundColor)
        ) {
            Column(
                modifier = Modifier.padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = settings.buttonColor,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Reading text...",
                    color = settings.textColor,
                    fontSize = Constants.FontSizes.label
                )
            }
        }
    }
}
