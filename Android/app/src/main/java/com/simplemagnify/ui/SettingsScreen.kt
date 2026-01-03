package com.simplemagnify.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simplemagnify.components.BigSlider
import com.simplemagnify.components.SettingsSegmentedControl
import com.simplemagnify.components.SettingsToggle
import com.simplemagnify.models.AppSettings
import com.simplemagnify.utils.Constants
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    settings: AppSettings,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(settings.backgroundColor)
    ) {
        // Header
        SettingsHeader(
            settings = settings,
            onBack = onBack
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(Constants.Dimensions.screenPadding)
        ) {
            // Default Zoom
            Text(
                text = Constants.Strings.DEFAULT_ZOOM,
                fontSize = Constants.FontSizes.label,
                color = settings.textColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            BigSlider(
                value = settings.defaultZoom,
                onValueChange = { value ->
                    scope.launch {
                        settings.updateDefaultZoom(value)
                    }
                },
                settings = settings
            )

            Spacer(modifier = Modifier.height(24.dp))

            Divider(color = settings.textColor.copy(alpha = 0.3f))

            Spacer(modifier = Modifier.height(24.dp))

            // Light on Start
            SettingsToggle(
                title = Constants.Strings.LIGHT_ON_START,
                isOn = settings.lightOnStart,
                settings = settings,
                onToggle = { value ->
                    scope.launch {
                        settings.updateLightOnStart(value)
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Divider(color = settings.textColor.copy(alpha = 0.3f))

            Spacer(modifier = Modifier.height(24.dp))

            // High Contrast Mode
            SettingsToggle(
                title = Constants.Strings.HIGH_CONTRAST_MODE,
                isOn = settings.highContrastMode,
                settings = settings,
                onToggle = { value ->
                    scope.launch {
                        settings.updateHighContrastMode(value)
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Divider(color = settings.textColor.copy(alpha = 0.3f))

            Spacer(modifier = Modifier.height(24.dp))

            // Button Position
            SettingsSegmentedControl(
                title = Constants.Strings.BUTTON_POSITION,
                selection = settings.buttonPosition,
                settings = settings,
                onSelectionChange = { value ->
                    scope.launch {
                        settings.updateButtonPosition(value)
                    }
                }
            )

            Spacer(modifier = Modifier.height(40.dp))

            // App Info
            AppInfoSection(settings = settings)
        }
    }
}

@Composable
private fun SettingsHeader(
    settings: AppSettings,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Constants.Dimensions.headerHeight)
            .padding(horizontal = Constants.Dimensions.screenPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
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

        Text(
            text = Constants.Strings.SETTINGS,
            fontSize = Constants.FontSizes.header,
            fontWeight = FontWeight.SemiBold,
            color = settings.textColor
        )

        // Spacer for alignment
        Spacer(modifier = Modifier.width(Constants.Dimensions.minTapTarget))
    }
}

@Composable
private fun AppInfoSection(settings: AppSettings) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = Constants.Strings.APP_NAME,
            fontSize = Constants.FontSizes.label,
            color = settings.textColor
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Version 1.0.0",
            fontSize = 14.sp,
            color = settings.textColor.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Made with care for seniors",
            fontSize = 14.sp,
            color = settings.textColor.copy(alpha = 0.6f)
        )
    }
}
