package com.jscoding.simplealarm.presentation.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.jscoding.simplealarm.presentation.screens.settings.ringtone.RingtonePickerScreen
import com.jscoding.simplealarm.presentation.screens.settings.thememode.SettingsThemeModeScreen

fun NavGraphBuilder.settingsNavGraph(
    route: String,
    navController: NavHostController,
) {
    navigation(
        route = route,
        startDestination = "nav_settings_home",
    ) {
        composable("nav_settings_home") {
            val viewModel = it.sharedSettingsViewModel<SettingsViewModel>(navController)
            SettingsScreen(
                settingsViewModel = viewModel,
                navigateToThemeMode = {
                    navController.navigate("nav_settings_theme_mode")
                },
                navigateToRingtone = {
                    navController.navigate("nav_settings_ringtone")
                },
                onBack = { navController.navigateUp() }
            )
        }

        composable("nav_settings_theme_mode") {
            val viewModel = it.sharedSettingsViewModel<SettingsViewModel>(navController)
            SettingsThemeModeScreen(
                viewModel = viewModel,
                onBack = { navController.navigateUp() },
            )
        }

        composable("nav_settings_ringtone") {
            RingtonePickerScreen(
                title = "Default ringtone",
                onBack = { navController.navigateUp() },
            )
        }
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedSettingsViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(parentEntry)
}