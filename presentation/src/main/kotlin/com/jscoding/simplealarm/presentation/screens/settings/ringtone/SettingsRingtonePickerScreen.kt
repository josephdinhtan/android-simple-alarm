package com.jscoding.simplealarm.presentation.screens.settings.ringtone

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsRingtonePickerRoute(
    ringtonePickerViewModel: SettingsRingtonePickerViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    LaunchedEffect(Unit) {
        ringtonePickerViewModel.getDefaultRingtone()
    }
    RingtonePickerScreen(
        screenTitle = "Default ringtone",
        ringtones = ringtonePickerViewModel.availableRingtones.collectAsState().value,
        isTonePlaying = ringtonePickerViewModel.isTonePlaying.collectAsState().value,
        selectedRingtone = ringtonePickerViewModel.selectedRingtone.collectAsState().value,
        onRingtoneSelected = { ringtonePickerViewModel.onRingtoneSelectedAndPlayTone(it) },
        onStopPlaying = { ringtonePickerViewModel.stopPlayTone() },
        onSave = {
            ringtonePickerViewModel.setDefaultRingtone(
                ringtonePickerViewModel.selectedRingtone.value
            )
            ringtonePickerViewModel.stopPlayTone()
            onBack()
        },
        onBack = {
            ringtonePickerViewModel.stopPlayTone()
            onBack()
        },
    )
}