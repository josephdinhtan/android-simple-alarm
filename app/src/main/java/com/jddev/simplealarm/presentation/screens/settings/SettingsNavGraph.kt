package com.jddev.simplealarm.presentation.screens.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation

fun NavGraphBuilder.settingsNavGraph(
    route: String,
    navController: NavHostController,
) {
    navigation(
        route = route,
        startDestination = "nav_settings_home",
    ) {
        composable("nav_settings_home") {
            SettingsScreen(
                onBack = { navController.navigateUp() }
            )
        }
    }
}