package com.jddev.simplealarm.presentation.screens.settings.ringtone

import android.net.Uri
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jddev.simplealarm.domain.model.alarm.Ringtone
import com.jddev.simpletouch.ui.customization.settingsui.StSettingsUi
import com.jddev.simpletouch.ui.customization.settingsui.group.StSettingsGroup
import com.jddev.simpletouch.ui.utils.StUiPreview
import com.jddev.simpletouch.ui.utils.StUiPreviewWrapper

@Composable
fun RingtonePickerScreen(
    alarmId: Long = -1L,
    ringtonePickerViewModel: RingtonePickerViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    val isFromAlarmEditScreen = alarmId != -1L
    LaunchedEffect(Unit) {
        if (isFromAlarmEditScreen) {
            ringtonePickerViewModel.getAlarmSelectedRingtone(alarmId)
        } else {
            ringtonePickerViewModel.getDefaultRingtone()
        }
    }
    RingtonePickerScreen(
        screenTitle = if (isFromAlarmEditScreen) "Alarm ringtone" else "Default ringtone",
        ringtones = ringtonePickerViewModel.availableRingtones.collectAsState().value,
        selectedRingtone = ringtonePickerViewModel.selectedRingtone.collectAsState().value,
        onRingtoneSelected = { ringtonePickerViewModel.onRingtoneSelected(it) },
        onSave = {
            if (isFromAlarmEditScreen) {
                ringtonePickerViewModel.setAlarmRingtone(
                    ringtonePickerViewModel.selectedRingtone.value,
                    alarmId
                )
            } else {
                ringtonePickerViewModel.setDefaultRingtone(
                    ringtonePickerViewModel.selectedRingtone.value
                )
            }
            onBack()
        },
        onCancel = onBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RingtonePickerScreen(
    screenTitle: String,
    ringtones: List<Ringtone>,
    selectedRingtone: Ringtone,
    onRingtoneSelected: (Ringtone) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(contentWindowInsets = WindowInsets.safeContent, topBar = {
        CenterAlignedTopAppBar(
            title = { Text(screenTitle) },
            scrollBehavior = scrollBehavior,
            navigationIcon = {
                IconButton(onClick = onCancel) {
                    Icon(Icons.Default.Close, contentDescription = "Cancel")
                }
            },
            actions = {
                IconButton(onClick = {
                    onSave()
                }) {
                    Icon(Icons.Default.Check, contentDescription = "Save")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
        )
    }) { innerPadding ->
        StSettingsUi(
            Modifier
                .padding(innerPadding)
                .selectableGroup(),
            scrollBehavior = scrollBehavior,
        ) {
            StSettingsGroup {
                ringtones.forEach { ringtone ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = (ringtone.uri == selectedRingtone.uri), onClick = {
                                    onRingtoneSelected(ringtone)
                                }, role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (ringtone.uri == selectedRingtone.uri), onClick = null
                        )
                        Text(
                            text = ringtone.title,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
@StUiPreview
private fun Preview() {
    val ringtones = List(20, { Ringtone("Ringtone $it", Uri.parse("uri_test")) })
    StUiPreviewWrapper {
        RingtonePickerScreen(
            screenTitle = "Ringtone",
            ringtones = ringtones,
            selectedRingtone = Ringtone.Silent,
            onRingtoneSelected = {},
            onSave = {},
            onCancel = {},
        )
    }
}