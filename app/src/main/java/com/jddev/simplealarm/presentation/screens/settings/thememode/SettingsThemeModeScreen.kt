package com.jddev.simplealarm.presentation.screens.settings.thememode

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jddev.simplealarm.domain.model.settings.ThemeMode
import com.jddev.simplealarm.presentation.screens.settings.SettingsViewModel
import com.jddev.simpletouch.ui.customization.settingsui.StSettingsUi
import com.jddev.simpletouch.ui.customization.settingsui.group.StSettingsGroup
import com.jddev.simpletouch.ui.foundation.StUiScaffold

@Composable
fun SettingsThemeModeScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    val themeMode = viewModel.themeMode.collectAsState()
    SettingsThemeModeScreen(
        appThemeMode = themeMode.value,
        onBack = onBack,
        onThemeChange = {
            viewModel.setThemeSetting(it)
            onBack()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsThemeModeScreen(
    appThemeMode: ThemeMode,
    onBack: () -> Unit,
    onThemeChange: (ThemeMode) -> Unit,
) {
    val radioOptions = listOf(
        Pair("Dark mode", ThemeMode.DARK),
        Pair("Light mode", ThemeMode.LIGHT),
        Pair("Follow system", ThemeMode.SYSTEM)
    )
    var selectedOption by remember { mutableStateOf(appThemeMode) }

    StUiScaffold(
        title = "Theme mode",
        onBack = onBack,
    ) {
        // Note that Modifier.selectableGroup() is essential to ensure correct accessibility behavior
        StSettingsUi(
            Modifier
                .padding(it)
                .selectableGroup(),
        ) {
            StSettingsGroup {
                radioOptions.forEach { themeModeOption ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = (themeModeOption.second == selectedOption),
                                onClick = {
                                    selectedOption = themeModeOption.second
                                    onThemeChange(themeModeOption.second)
                                },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (themeModeOption.second == selectedOption),
                            onClick = null
                        )
                        Text(
                            text = themeModeOption.first,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        }
    }
}