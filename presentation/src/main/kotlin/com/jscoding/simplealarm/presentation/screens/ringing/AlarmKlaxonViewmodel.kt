package com.jscoding.simplealarm.presentation.screens.ringing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jscoding.simplealarm.domain.model.alarm.Alarm
import com.jscoding.simplealarm.domain.usecase.alarm.DismissAlarmUseCase
import com.jscoding.simplealarm.domain.usecase.alarm.GetAlarmByIdUseCase
import com.jscoding.simplealarm.domain.usecase.alarm.SnoozeAlarmUseCase
import com.jscoding.simplealarm.presentation.utils.toStringTimeDisplay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AlarmKlaxonState {
    data object None : AlarmKlaxonState
    data object Snoozed : AlarmKlaxonState
    data object Dismissed : AlarmKlaxonState

    data class Ringing(
        val timeStringDisplay: String,
    ) : AlarmKlaxonState
}

@HiltViewModel
class AlarmKlaxonViewmodel @Inject constructor(
    private val dismissAlarmUseCase: DismissAlarmUseCase,
    private val snoozeAlarmUseCase: SnoozeAlarmUseCase,
    private val getAlarmByIdUseCase: GetAlarmByIdUseCase,
) : ViewModel() {

    private val _alarmKlaxonState = MutableStateFlow<AlarmKlaxonState>(AlarmKlaxonState.None)
    val alarmKlaxonState = _alarmKlaxonState.asStateFlow()

    private val _alarm = MutableStateFlow<Alarm?>(null)
    val alarm = _alarm.asStateFlow()

    fun setupAlarm(alarmId: Long) {
        viewModelScope.launch {
            _alarm.value = getAlarmByIdUseCase(alarmId)
            _alarm.value?.let {
                // TODO: hardcode is24HourFormat here
                _alarmKlaxonState.value = AlarmKlaxonState.Ringing(it.toStringTimeDisplay(false))
            }
        }
    }

    fun dismissAlarm(alarmId: Long) {
        viewModelScope.launch {
            dismissAlarmUseCase(alarmId)
            _alarmKlaxonState.value = AlarmKlaxonState.Dismissed
        }
    }

    fun snoozeAlarm(alarmId: Long) {
        viewModelScope.launch {
            snoozeAlarmUseCase(alarmId)
            _alarmKlaxonState.value = AlarmKlaxonState.Snoozed
        }
    }
}