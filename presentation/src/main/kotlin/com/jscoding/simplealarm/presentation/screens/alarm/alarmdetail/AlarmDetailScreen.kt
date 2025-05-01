package com.jscoding.simplealarm.presentation.screens.alarm.alarmdetail

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.MoreTime
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Vibration
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jddev.simpletouch.ui.customization.settingsui.StSettingsUi
import com.jddev.simpletouch.ui.customization.settingsui.group.StSettingsGroup
import com.jddev.simpletouch.ui.customization.settingsui.navigation.StSettingsNavigateItem
import com.jddev.simpletouch.ui.customization.settingsui.switch.StSettingsSwitchItem
import com.jddev.simpletouch.ui.utils.StUiPreview
import com.jddev.simpletouch.ui.utils.StUiPreviewWrapper
import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.entity.alarm.DayOfWeek
import com.jscoding.simplealarm.presentation.components.WheelTimePicker
import com.jscoding.simplealarm.presentation.utils.default
import com.jscoding.simplealarm.presentation.utils.toDisplayString
import kotlinx.coroutines.flow.collectLatest
import kotlin.time.Duration.Companion.minutes

@Composable
fun DetailAlarmRoute(
    viewModel: AlarmDetailViewModel,
    navigateToRingtone: () -> Unit,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val is24hFormat by viewModel.is24hFormat.collectAsState()
    val context = LocalContext.current

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, "Notification permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { state ->
            when (state) {
                is AlarmDetailViewModel.AlarmDetailEvent.SaveSuccess -> {
                    Toast.makeText(
                        context, "Alarm saved successfully", Toast.LENGTH_SHORT
                    ).show()
                    onBack()
                }

                is AlarmDetailViewModel.AlarmDetailEvent.Error -> {
                    Toast.makeText(
                        context, state.message, Toast.LENGTH_SHORT
                    ).show()
                    if (state.needExit) {
                        onBack()
                    }
                }

                is AlarmDetailViewModel.AlarmDetailEvent.NotificationNotAllow -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }
        }
    }

    when (uiState) {
        is AlarmDetailViewModel.UiState.Loading -> {
            Scaffold {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(it), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.width(64.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }
        }

        is AlarmDetailViewModel.UiState.Success -> {
            val alarm = (uiState as AlarmDetailViewModel.UiState.Success).alarm
            val isNewAlarm = (uiState as AlarmDetailViewModel.UiState.Success).isNewAlarm
            AlarmDetailScreen(alarm = alarm,
                is24hFormat = is24hFormat,
                navigateToRingtone = navigateToRingtone,
                onSave = {
                    viewModel.saveAlarm()
                },
                onDelete = if (isNewAlarm) {
                    null
                } else {
                    {
                        viewModel.deleteAlarm()
                        onBack()
                    }
                },
                onAlarmValueChange = {
                    viewModel.onAlarmValueChange(it)
                },
                onCancel = { onBack() })
        }

        is AlarmDetailViewModel.UiState.Error -> {
            val message = (uiState as AlarmDetailViewModel.UiState.Error).message
            Toast.makeText(LocalContext.current, message, Toast.LENGTH_SHORT).show()
            Scaffold {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(it),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Error",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(text = (uiState as AlarmDetailViewModel.UiState.Error).message)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun AlarmDetailScreen(
    alarm: Alarm,
    is24hFormat: Boolean,
    navigateToRingtone: () -> Unit,
    onSave: (Alarm) -> Unit,
    onAlarmValueChange: (Alarm) -> Unit,
    onDelete: ((Alarm?) -> Unit)? = null,
    onCancel: () -> Unit,
) {
    var showAlarmSnoozeLengthDialog by remember { mutableStateOf(false) }

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
        var repeatLabel by remember { mutableStateOf(alarm.repeatDays.toDisplayString("Every")) }
        StSettingsUi(
            modifier = Modifier.padding(padding), scrollBehavior = scrollBehavior
        ) {
            item {
                WheelTimePicker(initHour = alarm.hour,
                    initMinute = alarm.minute,
                    is24Hour = is24hFormat,
                    onTimeSelected = { hour, minute ->
                        onAlarmValueChange(alarm.copy(hour = hour, minute = minute))
                    })
            }
            item {
                AlarmNameTextField(
                    alarmName = alarm.label,
                    onAlarmNameChange = { onAlarmValueChange(alarm.copy(label = it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            // Repeat days
            StSettingsGroup(
                header = repeatLabel,
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
                                repeatLabel = repeatDays.toDisplayString("Every")
                                onAlarmValueChange(alarm.copy(repeatDays = repeatDays))
                            },
                            label = { Text(day.name.take(3)) })
                    }
                }
            }

            StSettingsGroup {
                StSettingsNavigateItem(
                    title = "Ringtone",
                    leadingImageVector = Icons.Outlined.NotificationsActive,
                    subTitle = alarm.ringtone.title,
                    onClick = navigateToRingtone
                )
                StSettingsSwitchItem(title = "Vibrate",
                    leadingImageVector = Icons.Outlined.Vibration,
                    checked = alarm.vibration,
                    onCheckedChange = {
                        onAlarmValueChange(alarm.copy(vibration = it))
                    })
                StSettingsNavigateItem(title = "Snooze length",
                    leadingImageVector = Icons.Outlined.MoreTime,
                    subTitle = "${alarm.snoozeTime.inWholeMinutes} minutes",
                    onClick = {
                        showAlarmSnoozeLengthDialog = true
                    }
                )
            }
        }
    }

    AlarmSnoozeLengthDialog(
        showDialog = showAlarmSnoozeLengthDialog,
        selectedSnoozeLength = alarm.snoozeTime.inWholeMinutes.toInt(),
        onDismissRequest = { showAlarmSnoozeLengthDialog = false },
        onSelected = {
            onAlarmValueChange(alarm.copy(snoozeTime = it.minutes))
            showAlarmSnoozeLengthDialog = false
        }
    )
}

@Composable
private fun AlarmNameTextField(
    modifier: Modifier = Modifier,
    alarmName: String,
    onAlarmNameChange: (String) -> Unit,
    isError: Boolean = false,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = alarmName,
        onValueChange = { newValue ->
            if (newValue.length <= 30) { // Limit to 30 characters
                onAlarmNameChange(newValue)
            }
        },
        modifier = modifier
            .semantics { contentDescription = "Alarm name input field" },
        label = { Text("Alarm name") },
        placeholder = {
            Text(
                "Enter alarm name", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        },
        singleLine = true,
        isError = isError,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = {
            keyboardController?.hide()
        }),
        trailingIcon = {
            if (alarmName.isNotEmpty()) {
                IconButton(onClick = { onAlarmNameChange("") },
                    modifier = Modifier.semantics { contentDescription = "Clear alarm name" }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        shape = RoundedCornerShape(8.dp)
    )
}

@StUiPreview
@Composable
private fun Preview() {
    StUiPreviewWrapper {
        AlarmDetailScreen(Alarm.default().copy(
            hour = 23, minute = 56, repeatDays = listOf(
                DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY
            )
        ), is24hFormat = false, {}, {}, {}, {}, {})
    }
}