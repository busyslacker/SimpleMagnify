package com.simplemagnify.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.simplemagnify.utils.Constants

private val LightColorScheme = lightColorScheme(
    primary = Constants.Colors.buttonPrimary,
    onPrimary = Color.White,
    secondary = Constants.Colors.buttonPrimary,
    onSecondary = Color.White,
    background = Constants.Colors.background,
    onBackground = Constants.Colors.textPrimary,
    surface = Constants.Colors.background,
    onSurface = Constants.Colors.textPrimary
)

@Composable
fun SimpleMagnifyTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
