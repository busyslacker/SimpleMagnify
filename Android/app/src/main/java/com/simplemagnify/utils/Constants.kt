package com.simplemagnify.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object Constants {
    // Dimensions
    object Dimensions {
        val minTapTarget = 60.dp
        val buttonHeight = 80.dp
        val buttonSpacing = 16.dp
        val screenPadding = 20.dp
        val sliderHeight = 60.dp
        val sliderThumbSize = 40.dp
        val headerHeight = 60.dp
        val cornerRadius = 12.dp
    }

    // Zoom
    object Zoom {
        const val MIN = 1.0f
        const val MAX = 10.0f
        const val DEFAULT = 2.0f
    }

    // Colors - Standard Mode
    object Colors {
        val background = Color.White
        val textPrimary = Color(0xFF1A1A1A)
        val buttonPrimary = Color(0xFF0066CC)
        val buttonDanger = Color(0xFFCC0000)
        val buttonBackground = Color(0xFFF0F0F0)
        val sliderTrack = Color(0xFFE0E0E0)
        val sliderFill = Color(0xFF0066CC)

        // High Contrast Mode
        val hcBackground = Color.Black
        val hcText = Color.Yellow
        val hcButtonPrimary = Color.Yellow
        val hcButtonBackground = Color(0xFF333333)
    }

    // Font Sizes
    object FontSizes {
        val header = 24.sp
        val button = 20.sp
        val label = 18.sp
        val sliderLabel = 16.sp
    }

    // Strings
    object Strings {
        const val APP_NAME = "SimpleMagnify"
        const val LIGHT = "LIGHT"
        const val FREEZE = "FREEZE"
        const val BACK_TO_CAMERA = "BACK TO CAMERA"
        const val SETTINGS = "Settings"
        const val DEFAULT_ZOOM = "Default Zoom"
        const val LIGHT_ON_START = "Light on at start"
        const val HIGH_CONTRAST_MODE = "High contrast mode"
        const val BUTTON_POSITION = "Button Position"
        const val LEFT = "LEFT"
        const val RIGHT = "RIGHT"
    }

    // DataStore Keys
    object StorageKeys {
        const val DEFAULT_ZOOM = "default_zoom"
        const val LIGHT_ON_START = "light_on_start"
        const val HIGH_CONTRAST_MODE = "high_contrast"
        const val BUTTON_POSITION = "button_position"
    }
}
