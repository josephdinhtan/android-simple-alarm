package com.jscoding.simplealarm.presentation.screens.alarm.alarmdetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jddev.simpletouch.ui.customization.settingsui.StSettingsUi
import com.jddev.simpletouch.ui.customization.settingsui.group.StSettingsGroup
import com.jddev.simpletouch.ui.customization.settingsui.navigation.StSettingsNavigateItem
import com.jddev.simpletouch.ui.customization.settingsui.switch.StSettingsSwitchItem
import com.jddev.simpletouch.ui.foundation.dialog.StUiEmptyDialog
import com.jddev.simpletouch.ui.utils.StUiPreview
import com.jddev.simpletouch.ui.utils.StUiPreviewWrapper
import com.jscoding.simplealarm.domain.model.DayOfWeek
import com.jscoding.simplealarm.domain.model.alarm.Alarm
import com.jscoding.simplealarm.presentation.components.DialTimePicker
import com.jscoding.simplealarm.presentation.utils.default

@Composable
fun AddNewAlarmRoute(
    alarmDetailViewModel: AlarmDetailScreenViewModel = hiltViewModel(),
    navigateToRingtone: () -> Unit,
    onBack: () -> Unit,
) {
    val alarm by alarmDetailViewModel.alarm.collectAsState()
    LaunchedEffect(Unit) {
        alarmDetailViewModel.setupNewAlarm()
    }
    AlarmDetailScreen(alarm = alarm, navigateToRingtone = navigateToRingtone, onSave = {
        alarmDetailViewModel.addNewAlarm(it)
        onBack()
    }, onAlarmValueChange = {
        alarmDetailViewModel.onAlarmValueChange(it)
    }, onCancel = { onBack() })
}

@Composable
fun EditAlarmRoute(
    alarmDetailViewModel: AlarmDetailScreenViewModel = hiltViewModel(),
    alarmId: Long,
    navigateToRingtone: () -> Unit,
    onBack: () -> Unit,
) {
    val alarm by alarmDetailViewModel.alarm.collectAsState()
    LaunchedEffect(Unit) {
        alarmDetailViewModel.editAlarm(alarmId)
    }
    AlarmDetailScreen(alarm = alarm, navigateToRingtone = navigateToRingtone, onDelete = {
        alarmDetailViewModel.deleteAlarm(it)
        onBack()
    }, onSave = {
        alarmDetailViewModel.updateAlarm(it)
        onBack()
    }, onAlarmValueChange = {
        alarmDetailViewModel.onAlarmValueChange(it)
    }, onCancel = { onBack() })
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun AlarmDetailScreen(
    alarm: Alarm,
    navigateToRingtone: () -> Unit,
    onSave: (Alarm) -> Unit,
    onAlarmValueChange: (Alarm) -> Unit,
    onDelete: ((Alarm?) -> Unit)? = null,
    onCancel: () -> Unit,
) {
    var showTimePicker by remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(contentWindowInsets = WindowInsets.safeDrawing, topBar = {
        CenterAlignedTopAppBar(
            title = { Text("Edit Alarm") },
            scrollBehavior = scrollBehavior,
            navigationIcon = {
                IconButton(onClick = onCancel) {
                    Icon(Icons.Default.Close, contentDescription = "Cancel")
                }
            },
            actions = {
                onDelete?.let {
                    IconButton(onClick = {
                        onDelete(alarm)
                    }) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete")
                    }
                }
                IconButton(onClick = {
                    onSave(alarm)
                }) {
                    Icon(Icons.Default.Check, contentDescription = "Save")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
        )
    }) { padding ->
        StSettingsUi(
            modifier = Modifier.padding(padding), scrollBehavior = scrollBehavior
        ) {
            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .clickable {
                            showTimePicker = true
                        },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 32.dp),
                        text = "${alarm.hour.toString().padStart(2, '0')}:${
                            alarm.minute.toString().padStart(2, '0')
                        }",
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 84.sp)
                    )
                }
            }
            item {
                OutlinedTextField(modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                    value = alarm.label,
                    onValueChange = { onAlarmValueChange(alarm.copy(label = it)) },
                    label = { Text("Label") },
//                    shape = CircleShape,
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.AutoMirrored.Filled.Label, contentDescription = null)
                    })
            }

            // Repeat days
            StSettingsGroup(
                header = "Repeat",
            ) {
                FlowRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    DayOfWeek.entries.forEach { day ->
                        FilterChip(selected = alarm.repeatDays.contains(day),
                            shape = CircleShape,
                            colors = FilterChipDefaults.filterChipColors().copy(
                                selectedContainerColor = MaterialTheme.colorScheme.tertiary,
                                selectedLabelColor = MaterialTheme.colorScheme.onTertiary,
                            ),
                            onClick = {
                                val repeatDays = alarm.repeatDays.toMutableSet().apply {
                                    if (contains(day)) remove(day) else add(day)
                                }.toList()
                                onAlarmValueChange(alarm.copy(repeatDays = repeatDays))
                            },
                            label = { Text(day.name.take(3)) })
                    }
                }
            }

            StSettingsGroup {
                StSettingsNavigateItem(
                    title = "Ringtone",
                    subTitle = alarm.ringtone.title ?: "Unknown",
                    onClick = navigateToRingtone
                )
                StSettingsSwitchItem(
                    title = "Vibrate",
                    checked = alarm.vibration,
                    onCheckedChange = {
                        onAlarmValueChange(alarm.copy(vibration = it))
                    })
                StSettingsSwitchItem(
                    title = "Snooze",
                    checked = alarm.vibration,
                    onCheckedChange = {})
            }
        }
    }

    StUiEmptyDialog(
        showDialog = showTimePicker,
        onDismissRequest = { showTimePicker = false }
    ) {
        DialTimePicker(
            modifier = Modifier.padding(20.dp),
            initialHour = alarm.hour,
            initialMinute = alarm.minute,
            onConfirm = {
                onAlarmValueChange(alarm.copy(hour = it.hour, minute = it.minute))
                showTimePicker = false
            },
            onDismiss = {
                showTimePicker = false
            }
        )
    }
}

@StUiPreview
@Composable
private fun Preview() {
    StUiPreviewWrapper {
        AlarmDetailScreen(
            Alarm.default().copy(
                repeatDays = listOf(
                    DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY
                )
            ), {}, {}, {}, {}, {})
    }
}