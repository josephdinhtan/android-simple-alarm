package com.jscoding.simplealarm.presentation.screens.alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.repository.SettingsRepository
import com.jscoding.simplealarm.domain.usecase.alarm.GetAllAlarmsUseCase
import com.jscoding.simplealarm.domain.usecase.alarm.UpdateAlarmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val getAlarms: GetAllAlarmsUseCase,
    private val updateAlarm: UpdateAlarmUseCase,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _alarms = MutableStateFlow<List<Alarm>>(emptyList())
    val alarms = _alarms.asStateFlow()

    val is24hFormat = settingsRepository.is24HourFormat
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    init {
        viewModelScope.launch {
            getAlarms().collect { alarmList ->
                _alarms.value = alarmList
                    .sortedWith(
                        compareByDescending<Alarm> { it.enabled }  // 1. Enabled alarms first
                            .thenBy { it.nextTriggerTimeMillis() }   // 2. Sooner alarm first
                    )
            }
        }
    }

    fun onEnableUpdate(alarm: Alarm, enable: Boolean) {
        viewModelScope.launch { updateAlarm(alarm, alarm.copy(enabled = enable)) }
    }
}

fun Alarm.nextTriggerTimeMillis(): Long {
    val now = LocalDateTime.now()
    var nextTime = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)
    if (nextTime.isBefore(now)) {
        nextTime = nextTime.plusDays(1)
    }
    return nextTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}