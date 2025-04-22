package com.jscoding.simplealarm.presentation.screens.alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jscoding.simplealarm.domain.model.alarm.Alarm
import com.jscoding.simplealarm.domain.usecase.alarm.GetAllAlarmsUseCase
import com.jscoding.simplealarm.domain.usecase.alarm.UpdateAlarmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val getAlarms: GetAllAlarmsUseCase,
    private val updateAlarm: UpdateAlarmUseCase,
) : ViewModel() {

    private val _alarms = MutableStateFlow<List<Alarm>>(emptyList())
    val alarms = _alarms.asStateFlow()

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

    fun update(alarm: Alarm) {
        viewModelScope.launch { updateAlarm(alarm) }
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