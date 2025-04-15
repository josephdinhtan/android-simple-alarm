package com.jddev.simplealarm.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.jddev.simplealarm.domain.model.settings.ThemeMode
import com.jddev.simplealarm.presentation.screens.settings.SettingsViewModel
import com.jddev.simpletouch.ui.theme.StUiTheme

@Composable
fun SimpleAlarmApp(
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val themeMode = settingsViewModel.themeMode.collectAsStateWithLifecycle()
    val useDynamicColors = settingsViewModel.isUseDynamicColors.collectAsStateWithLifecycle()
    val isDarkTheme = when (themeMode.value) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    StUiTheme (
        isDarkTheme = isDarkTheme,
        useDynamicColors = useDynamicColors.value
    ) {
        val navController = rememberNavController()
        RootNavGraph(
            rootNavController = navController
        )
    }
}
