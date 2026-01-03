package com.simplemagnify.ui

import android.graphics.Bitmap
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.simplemagnify.models.AppSettings

sealed class Screen(val route: String) {
    object Camera : Screen("camera")
    object FrozenImage : Screen("frozen_image")
    object Settings : Screen("settings")
}

// Shared state holder for captured image
object CapturedImageHolder {
    var bitmap: Bitmap? by mutableStateOf(null)
}

@Composable
fun Navigation() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val settings = remember { AppSettings(context) }

    NavHost(
        navController = navController,
        startDestination = Screen.Camera.route
    ) {
        composable(Screen.Camera.route) {
            CameraScreen(
                settings = settings,
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToFrozenImage = {
                    navController.navigate(Screen.FrozenImage.route)
                }
            )
        }

        composable(Screen.FrozenImage.route) {
            FrozenImageScreen(
                bitmap = CapturedImageHolder.bitmap,
                settings = settings,
                onBack = {
                    CapturedImageHolder.bitmap = null
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                settings = settings,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
