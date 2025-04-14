package com.jddev.simplealarm.presentation.screens.alarm

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jddev.simplealarm.domain.model.Alarm
import com.jddev.simpletouch.ui.customization.settingsui.StSettingsUi
import com.jddev.simpletouch.ui.customization.settingsui.group.StSettingsGroup
import com.jddev.simpletouch.ui.customization.settingsui.switch.StSettingsSwitchItem
import com.jddev.simpletouch.ui.foundation.dialog.StUiEmptyDialog
import com.jddev.simpletouch.ui.foundation.topappbar.StUiCenterAlignedTopAppBar
import com.jddev.simpletouch.ui.utils.StUiPreview
import com.jddev.simpletouch.ui.utils.StUiPreviewWrapper
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.Calendar

@Composable
fun AddNewAlarmRoute(
    alarmViewModel: AlarmViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    EditAlarmScreen(alarm = null, onSave = {
        alarmViewModel.addNewAlarm(it)
        onBack()
    }, onCancel = { onBack() })
}

@Composable
fun EditAlarmRoute(
    alarmViewModel: AlarmViewModel = hiltViewModel(),
    alarmId: Long,
    onBack: () -> Unit,
) {
    val editingAlarm by alarmViewModel.editingAlarm.collectAsState()

    LaunchedEffect(alarmId) {
        alarmViewModel.getAlarm(alarmId)
    }

    if (editingAlarm == null) {
        Box(Modifier.fillMaxSize()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
        return
    }

    EditAlarmScreen(alarm = editingAlarm, onDelete = {
        alarmViewModel.delete(it)
        onBack()
    }, onSave = {
        alarmViewModel.update(it)
        onBack()
    }, onCancel = { onBack() })
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditAlarmScreen(
    alarm: Alarm?,
    onSave: (Alarm) -> Unit,
    onDelete: ((Alarm?) -> Unit)? = null,
    onCancel: () -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }
    var timeHour by remember { mutableIntStateOf(alarm?.hour ?: LocalTime.now().hour) }
    var timeMinute by remember { mutableIntStateOf(alarm?.minute ?: LocalTime.now().minute) }
    var label by remember { mutableStateOf(alarm?.label ?: "") }
    var repeatDays by remember { mutableStateOf(alarm?.repeatDays?.toSet() ?: emptySet()) }

    var isVibration by remember { mutableStateOf(true) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(contentWindowInsets = WindowInsets.safeContent, topBar = {
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
                    onSave(
                        Alarm(
                            id = alarm?.id ?: 0,
                            hour = timeHour,
                            minute = timeMinute,
                            label = label,
                            repeatDays = repeatDays.toList(),
                            isEnabled = true // or keep alarm.isEnabled if editing
                        )
                    )
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
                    text = "${timeHour.toString().padStart(2, '0')}:${
                        timeMinute.toString().padStart(2, '0')
                    }", style = MaterialTheme.typography.displayLarge
                )
            }

            OutlinedTextField(modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
                value = label,
                onValueChange = { label = it },
                label = { Text("Label") },
                shape = CircleShape,
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.AutoMirrored.Filled.Label, contentDescription = null)
                })

            // Repeat days
            StSettingsGroup(
                header = "Repeat",
            ) {
                FlowRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    DayOfWeek.entries.forEach { day ->
                        FilterChip(selected = repeatDays.contains(day),
                            shape = CircleShape,
                            colors = FilterChipDefaults.filterChipColors()
                                .copy(
                                    selectedContainerColor = MaterialTheme.colorScheme.tertiary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onTertiary,
                                ),
                            onClick = {
                                repeatDays = repeatDays.toMutableSet().apply {
                                    if (contains(day)) remove(day) else add(day)
                                }
                            },
                            label = { Text(day.name.take(3)) })
                    }
                }
            }

            StSettingsUi {
                StSettingsSwitchItem(title = "Vibrate", checked = isVibration, onCheckedChange = {
                    isVibration = it
                })
                StSettingsSwitchItem(title = "Alarm sound",
                    checked = isVibration,
                    onCheckedChange = {
                        isVibration = it
                    })
                StSettingsSwitchItem(title = "Snooze", checked = isVibration, onCheckedChange = {
                    isVibration = it
                })
            }
        }
    }

    StUiEmptyDialog(showDialog = showTimePicker, onDismissRequest = { showTimePicker = false }) {
        DialTimePicker(modifier = Modifier.padding(20.dp), onConfirm = {
            timeHour = it.hour
            timeMinute = it.minute
            showTimePicker = false
        }, onDismiss = {
            showTimePicker = false
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialTimePicker(
    modifier: Modifier = Modifier,
    onConfirm: (TimePickerState) -> Unit,
    onDismiss: () -> Unit,
) {
    var isDialMethode by remember { mutableStateOf(true) }
    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        AnimatedVisibility(isDialMethode) {
            TimePicker(state = timePickerState)
        }
        AnimatedVisibility(!isDialMethode) {
            TimeInput(state = timePickerState)
        }
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { isDialMethode = !isDialMethode }) {
                AnimatedVisibility(isDialMethode) {
                    Icon(Icons.Outlined.Keyboard, "Keyboard")
                }
                AnimatedVisibility(!isDialMethode) {
                    Icon(Icons.Outlined.AccessTime, "Dial")
                }
            }
            Spacer(Modifier.weight(1f))
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
            Spacer(Modifier.width(8.dp))
            TextButton(onClick = { onConfirm(timePickerState) }) {
                Text("OK")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@StUiPreview
@Composable
private fun PreviewDial() {
    StUiPreviewWrapper {
        DialTimePicker(Modifier, {}, {})
    }
}

@StUiPreview
@Composable
private fun Preview() {
    StUiPreviewWrapper {
        EditAlarmScreen(Alarm(
            1, 12, 0, "Test", listOf(
                DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY
            )
        ), {}, {}, {})
    }
}