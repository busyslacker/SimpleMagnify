package com.simplemagnify.models

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.simplemagnify.utils.Constants
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class ButtonPosition {
    LEFT, RIGHT
}

class AppSettings(private val context: Context) {
    private val defaultZoomKey = floatPreferencesKey(Constants.StorageKeys.DEFAULT_ZOOM)
    private val lightOnStartKey = booleanPreferencesKey(Constants.StorageKeys.LIGHT_ON_START)
    private val highContrastModeKey = booleanPreferencesKey(Constants.StorageKeys.HIGH_CONTRAST_MODE)
    private val buttonPositionKey = stringPreferencesKey(Constants.StorageKeys.BUTTON_POSITION)

    var defaultZoom by mutableStateOf(Constants.Zoom.DEFAULT)
        private set

    var lightOnStart by mutableStateOf(false)
        private set

    var highContrastMode by mutableStateOf(false)
        private set

    var buttonPosition by mutableStateOf(ButtonPosition.RIGHT)
        private set

    init {
        runBlocking {
            loadSettings()
        }
    }

    private suspend fun loadSettings() {
        val prefs = context.dataStore.data.first()
        defaultZoom = prefs[defaultZoomKey] ?: Constants.Zoom.DEFAULT
        lightOnStart = prefs[lightOnStartKey] ?: false
        highContrastMode = prefs[highContrastModeKey] ?: false
        buttonPosition = ButtonPosition.valueOf(
            prefs[buttonPositionKey] ?: ButtonPosition.RIGHT.name
        )
    }

    suspend fun updateDefaultZoom(value: Float) {
        defaultZoom = value
        context.dataStore.edit { prefs ->
            prefs[defaultZoomKey] = value
        }
    }

    suspend fun updateLightOnStart(value: Boolean) {
        lightOnStart = value
        context.dataStore.edit { prefs ->
            prefs[lightOnStartKey] = value
        }
    }

    suspend fun updateHighContrastMode(value: Boolean) {
        highContrastMode = value
        context.dataStore.edit { prefs ->
            prefs[highContrastModeKey] = value
        }
    }

    suspend fun updateButtonPosition(value: ButtonPosition) {
        buttonPosition = value
        context.dataStore.edit { prefs ->
            prefs[buttonPositionKey] = value.name
        }
    }

    // Computed colors based on mode
    val backgroundColor: Color
        get() = if (highContrastMode) Constants.Colors.hcBackground else Constants.Colors.background

    val textColor: Color
        get() = if (highContrastMode) Constants.Colors.hcText else Constants.Colors.textPrimary

    val buttonColor: Color
        get() = if (highContrastMode) Constants.Colors.hcButtonPrimary else Constants.Colors.buttonPrimary

    val buttonBackgroundColor: Color
        get() = if (highContrastMode) Constants.Colors.hcButtonBackground else Constants.Colors.buttonBackground
}
