package com.jddev.simplealarm.presentation.screens.settings

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.DynamicForm
import androidx.compose.material.icons.outlined.Timelapse
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.jddev.simplealarm.domain.model.settings.ThemeMode
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
    onBack: () -> Unit,
) {
    val themeMode = settingsViewModel.themeMode.collectAsState()
    val is24hFormat = settingsViewModel.is24hFormat.collectAsState()
    val isUseDynamicColors = settingsViewModel.isUseDynamicColors.collectAsState()
    SettingsScreen(
        themeMode = themeMode.value,
        is24hFormat = is24hFormat.value,
        isUseDynamicColors = isUseDynamicColors.value,
        navigateToThemeMode = navigateToThemeMode,
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
    navigateToThemeMode: () -> Unit,
    on24hFormatChange: (Boolean) -> Unit,
    onUseDynamicColorsChange: (Boolean) -> Unit,
    onBack: () -> Unit,
) {
    val scrollBehavior = stUiLargeTopAppbarScrollBehavior()
    Scaffold(
        topBar = {
            StUiLargeTopAppBar(
                scrollBehavior = scrollBehavior, title = "Settings", onBack = onBack
            )
        },
    ) { innerPadding ->
        var sliderPosition by remember { mutableFloatStateOf(40f) }

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
                    subTitle = "Chime Time",
                    onClick = {})
                StSettingsSliderItem(
                    title = "Default alarm volume",
                    leadingImageVector = Icons.Outlined.Alarm,
                    value = sliderPosition,
                    steps = 99,
                    valueRange = 0f..100f,
                    onValueChange = { sliderPosition = it },
                )
                StSettingsSliderItem(
                    title = "Gradually increase volume",
                    leadingImageVector = Icons.Outlined.Timelapse,
                    value = sliderPosition,
                    steps = 99,
                    valueRange = 0f..100f,
                    onValueChange = { sliderPosition = it },
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
