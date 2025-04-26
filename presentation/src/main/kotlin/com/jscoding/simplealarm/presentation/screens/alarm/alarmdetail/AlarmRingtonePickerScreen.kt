package com.jscoding.simplealarm.presentation.screens.alarm.alarmdetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.jscoding.simplealarm.presentation.screens.settings.ringtone.RingtonePickerScreen

@Composable
fun AlarmRingtonePickerRoute(
    viewModel: AlarmDetailViewModel,
    onBack: () -> Unit,
) {
    RingtonePickerScreen(
        screenTitle = "Alarm ringtone",
        ringtones = viewModel.availableRingtones.collectAsState().value,
        isTonePlaying = viewModel.isTonePlaying.collectAsState().value,
        selectedRingtone = viewModel.selectedRingtone.collectAsState().value,
        onRingtoneSelected = { viewModel.onRingtoneSelectedAndPlayTone(it) },
        onStopPlaying = { viewModel.stopPlayTone() },
        onSave = {
            viewModel.onRingtoneSave()
            viewModel.stopPlayTone()
            onBack()
        },
        onBack = {
            viewModel.stopPlayTone()
            onBack()
        },
    )
}