package com.jscoding.simplealarm.presentation.screens.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.jscoding.simplealarm.domain.entity.alarm.Ringtone
import com.jscoding.simplealarm.domain.entity.settings.ThemeMode
import com.jddev.simpletouch.ui.customization.settingsui.StSettingsUi
import com.jddev.simpletouch.ui.customization.settingsui.group.StSettingsGroup
import com.jddev.simpletouch.ui.customization.settingsui.navigation.StSettingsNavigateItem
import com.jddev.simpletouch.ui.customization.settingsui.slider.StSettingsSliderItem
import com.jddev.simpletouch.ui.customization.settingsui.switch.StSettingsSwitchItem
import com.jddev.simpletouch.ui.foundation.topappbar.StUiLargeTopAppBar
import com.jddev.simpletouch.ui.foundation.topappbar.stUiLargeTopAppbarScrollBehavior

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    navigateToThemeMode: () -> Unit,
    navigateToRingtone: () -> Unit,
    onBack: () -> Unit,
) {
    val themeMode = settingsViewModel.themeMode.collectAsState()
    val is24hFormat = settingsViewModel.is24hFormat.collectAsState()
    val isUseDynamicColors = settingsViewModel.isUseDynamicColors.collectAsState()
    val defaultRingtone = settingsViewModel.defaultRingtone.collectAsState()
    SettingsScreen(
        themeMode = themeMode.value,
        is24hFormat = is24hFormat.value,
        isUseDynamicColors = isUseDynamicColors.value,
        defaultRingtone = defaultRingtone.value,
        alarmVolume = settingsViewModel.currentAlarmVolume.collectAsState().value,
        maxAlarmVolume = settingsViewModel.maxAlarmVolume.collectAsState().value,
        setAlarmVolume = { settingsViewModel.setAlarmVolume(it) },
        navigateToThemeMode = navigateToThemeMode,
        navigateToRingtone = navigateToRingtone,
        on24hFormatChange = { settingsViewModel.on24hFormatChange(it) },
        onUseDynamicColorsChange = { settingsViewModel.setUseDynamicColors(it) },
        onBack = onBack,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    themeMode: ThemeMode,
    is24hFormat: Boolean,
    isUseDynamicColors: Boolean,
    defaultRingtone: Ringtone,
    alarmVolume: Int,
    maxAlarmVolume: Int,
    setAlarmVolume: (Int) -> Unit,
    navigateToThemeMode: () -> Unit,
    navigateToRingtone: () -> Unit,
    on24hFormatChange: (Boolean) -> Unit,
    onUseDynamicColorsChange: (Boolean) -> Unit,
    onBack: () -> Unit,
) {
    val scrollBehavior = stUiLargeTopAppbarScrollBehavior()
    Scaffold(
        topBar = {
            StUiLargeTopAppBar(
                scrollBehavior = scrollBehavior, title = "Settings", onBack = onBack,
            )
        },
    ) { innerPadding ->

        StSettingsUi(
            modifier = Modifier.padding(innerPadding),
            scrollBehavior = scrollBehavior,
        ) {
            StSettingsGroup(
                header = "General",
            ) {
                StSettingsNavigateItem(
                    leadingImageVector = Icons.Outlined.DarkMode,
                    title = "Theme",
                    subTitle = themeMode.toString(),
                    onClick = navigateToThemeMode
                )
                StSettingsSwitchItem(
                    leadingImageVector = Icons.Outlined.ColorLens,
                    title = "Use system colors",
                    checked = isUseDynamicColors,
                    onCheckedChange = onUseDynamicColorsChange,
                )
                StSettingsSwitchItem(
                    leadingImageVector = Icons.Outlined.AccessTime,
                    title = "24h format",
                    checked = is24hFormat,
                    onCheckedChange = on24hFormatChange,
                )
            }
            StSettingsGroup(
                header = "Alarms",
            ) {
                StSettingsNavigateItem(
                    title = "Silent after",
                    subTitle = "10 minutes",
                    onClick = {})
                StSettingsNavigateItem(
                    title = "Default ringtone",
                    subTitle = defaultRingtone.title,
                    onClick = navigateToRingtone)
                StSettingsSliderItem(
                    title = "Alarm volume $alarmVolume",
                    leadingImageVector = Icons.Outlined.Alarm,
                    value = alarmVolume.toFloat(),
                    steps = maxAlarmVolume - 1,
                    valueRange = 0f..maxAlarmVolume.toFloat(),
                    onValueChange = { setAlarmVolume(it.toInt()) },
                )
                StSettingsNavigateItem(
                    title = "Gradually increase volume",
                    subTitle = "5 seconds",
                    onClick = {}
                )
                StSettingsSwitchItem(
                    title = "Vibration",
                    checked = true,
                    onCheckedChange = {},
                )
            }
            StSettingsGroup(
                header = "Timer",
            ) {
                StSettingsNavigateItem(
                    title = "Sound",
                    subTitle = "Silent",
                    onClick = {})
                StSettingsSwitchItem(
                    title = "Vibration",
                    checked = true,
                    onCheckedChange = {},
                )
                StSettingsSwitchItem(
                    title = "Show mini Timer",
                    checked = false,
                    onCheckedChange = {},
                )
            }
            StSettingsGroup(
                header = "About",
            ) {
                StSettingsNavigateItem(
                    title = "Contact us",
                    onClick = {})
                StSettingsNavigateItem(
                    title = "Privacy policy",
                    onClick = {})
            }
        }
    }
}

//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_NO,
//    heightDp = 1300
//)
//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_YES,
//    heightDp = 1300
//)
//@Composable
//private fun Preview() {
//    StUiPreviewWrapper {
//        SettingsScreen(
//            themeMode = ThemeMode.SYSTEM,
//            navigateToThemeMode = {},
//            onBack = {},
//        )
//    }
//}
