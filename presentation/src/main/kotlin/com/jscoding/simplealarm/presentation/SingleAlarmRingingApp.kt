package com.jscoding.simplealarm.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jddev.simpletouch.ui.theme.StUiTheme
import com.jscoding.simplealarm.domain.entity.settings.ThemeMode
import com.jscoding.simplealarm.presentation.screens.ringing.AlarmRingingScreen
import com.jscoding.simplealarm.presentation.screens.settings.SettingsViewModel

@Composable
fun SingleAlarmRingingApp(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    alarmId: Long,
    onFinished: () -> Unit,
) {
    val themeMode = settingsViewModel.themeMode.collectAsStateWithLifecycle()
    val useDynamicColors = settingsViewModel.isUseDynamicColors.collectAsStateWithLifecycle()
    val isDarkTheme = when (themeMode.value) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    StUiTheme(
        isDarkTheme = isDarkTheme,
        useDynamicColors = useDynamicColors.value
    ) {
        AlarmRingingScreen(alarmId = alarmId, onFinished = onFinished)
    }
}