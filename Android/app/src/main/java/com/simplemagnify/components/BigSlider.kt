package com.simplemagnify.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.simplemagnify.models.AppSettings
import com.simplemagnify.utils.Constants

@Composable
fun BigSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    settings: AppSettings,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 1f..10f,
    minLabel: String = "1x",
    maxLabel: String = "10x"
) {
    val density = LocalDensity.current
    var sliderSize by remember { mutableStateOf(IntSize.Zero) }
    val thumbSizePx = with(density) { Constants.Dimensions.sliderThumbSize.toPx() }

    Column(modifier = modifier) {
        // Custom Slider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(Constants.Dimensions.sliderHeight)
                .onSizeChanged { sliderSize = it }
                .semantics {
                    contentDescription = "Zoom slider, current value ${String.format("%.1f", value)}x"
                    stateDescription = "${String.format("%.1f", value)}x zoom"
                }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val newValue = offsetToValue(
                            offset.x,
                            sliderSize.width.toFloat(),
                            thumbSizePx,
                            valueRange
                        )
                        onValueChange(newValue)
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        val newValue = offsetToValue(
                            change.position.x,
                            sliderSize.width.toFloat(),
                            thumbSizePx,
                            valueRange
                        )
                        onValueChange(newValue)
                    }
                },
            contentAlignment = Alignment.CenterStart
        ) {
            // Track background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Constants.Colors.sliderTrack)
            )

            // Filled track
            val progress = (value - valueRange.start) / (valueRange.endInclusive - valueRange.start)
            val trackWidth = if (sliderSize.width > 0) {
                ((sliderSize.width - thumbSizePx) * progress + thumbSizePx / 2).dp / density.density
            } else {
                0.dp
            }

            Box(
                modifier = Modifier
                    .width(trackWidth)
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(settings.buttonColor)
            )

            // Thumb
            val thumbOffset = if (sliderSize.width > 0) {
                ((sliderSize.width - thumbSizePx) * progress).dp / density.density
            } else {
                0.dp
            }

            Box(
                modifier = Modifier
                    .offset(x = thumbOffset)
                    .size(Constants.Dimensions.sliderThumbSize)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(settings.buttonColor)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = minLabel,
                fontSize = Constants.FontSizes.sliderLabel,
                color = settings.textColor
            )

            Text(
                text = String.format("%.1fx", value),
                fontSize = Constants.FontSizes.sliderLabel,
                fontWeight = FontWeight.Bold,
                color = settings.buttonColor
            )

            Text(
                text = maxLabel,
                fontSize = Constants.FontSizes.sliderLabel,
                color = settings.textColor
            )
        }
    }
}

private fun offsetToValue(
    offset: Float,
    width: Float,
    thumbSize: Float,
    range: ClosedFloatingPointRange<Float>
): Float {
    val adjustedWidth = width - thumbSize
    val clampedOffset = (offset - thumbSize / 2).coerceIn(0f, adjustedWidth)
    val normalizedValue = clampedOffset / adjustedWidth
    val rawValue = range.start + normalizedValue * (range.endInclusive - range.start)

    // Snap to 0.1 increments
    return (kotlin.math.round(rawValue * 10) / 10f).coerceIn(range)
}
