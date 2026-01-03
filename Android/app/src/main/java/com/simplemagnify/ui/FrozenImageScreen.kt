package com.simplemagnify.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.simplemagnify.components.BigButton
import com.simplemagnify.models.AppSettings
import com.simplemagnify.utils.Constants

@Composable
fun FrozenImageScreen(
    bitmap: Bitmap?,
    settings: AppSettings,
    onBack: () -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(settings.backgroundColor)
    ) {
        // Header
        FrozenImageHeader(
            settings = settings,
            onBack = onBack
        )

        // Image
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(androidx.compose.ui.graphics.Color.Black)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(1f, 10f)
                        if (scale > 1f) {
                            offset = Offset(
                                offset.x + pan.x,
                                offset.y + pan.y
                            )
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            if (scale > 1f) {
                                scale = 1f
                                offset = Offset.Zero
                            } else {
                                scale = 3f
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
                    color = androidx.compose.ui.graphics.Color.White,
                    fontSize = Constants.FontSizes.label
                )
            }
        }

        // Back Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(settings.backgroundColor)
                .padding(Constants.Dimensions.screenPadding)
        ) {
            BigButton(
                title = Constants.Strings.BACK_TO_CAMERA,
                icon = Icons.Default.ArrowBack,
                settings = settings,
                onClick = onBack
            )
        }
    }
}

@Composable
private fun FrozenImageHeader(
    settings: AppSettings,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Constants.Dimensions.headerHeight)
            .padding(horizontal = Constants.Dimensions.screenPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(Constants.Dimensions.minTapTarget)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
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
    }
}
