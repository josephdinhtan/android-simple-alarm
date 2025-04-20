package com.jscoding.simplealarm.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jddev.simpletouch.ui.theme.StUiTheme
import com.jscoding.simplealarm.domain.model.settings.ThemeMode
import com.jscoding.simplealarm.presentation.screens.settings.SettingsViewModel

@Composable
fun SingleAlarmRingingApp(
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
        Box(Modifier.fillMaxSize()) {
            Text("Ringing", Modifier.align(androidx.compose.ui.Alignment.Center))
        }
    }
}