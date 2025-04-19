package com.jscoding.simplealarm.presentation.screens.alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jscoding.simplealarm.domain.model.alarm.Alarm
import com.jscoding.simplealarm.domain.model.alarm.Ringtone
import com.jscoding.simplealarm.domain.repository.SettingsRepository
import com.jscoding.simplealarm.domain.usecase.alarm.AddAlarmUseCase
import com.jscoding.simplealarm.domain.usecase.alarm.DeleteAlarmUseCase
import com.jscoding.simplealarm.domain.usecase.alarm.GetAlarmByIdUseCase
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
    private val addAlarm: AddAlarmUseCase,
    private val getAlarmById: GetAlarmByIdUseCase,
    private val updateAlarm: UpdateAlarmUseCase,
    private val deleteAlarm: DeleteAlarmUseCase,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val defaultRingtone = settingsRepository.defaultRingtone
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Ringtone.Silent)

    private val _alarms = MutableStateFlow<List<Alarm>>(emptyList())
    val alarms = _alarms.asStateFlow()

    private val _editingAlarm = MutableStateFlow<Alarm?>(null)
    val editingAlarm = _editingAlarm.asStateFlow()

    init {
        viewModelScope.launch {
            getAlarms(Unit).collect { alarmList ->
                _alarms.value = alarmList
                    .sortedWith(
                        compareByDescending<Alarm> { it.enabled }  // 1. Enabled alarms first
                            .thenBy { it.nextTriggerTimeMillis() }   // 2. Sooner alarm first
                    )
            }
        }
    }

    fun getAlarm(id: Long) {
        if (id.toInt() == -1) {
            _editingAlarm.value = null
            return
        }
        viewModelScope.launch {
            _editingAlarm.value = getAlarmById(id)
        }
    }

    fun addNewAlarm(alarm: Alarm) {
        viewModelScope.launch { addAlarm(alarm) }
    }

    fun update(alarm: Alarm) {
        viewModelScope.launch { updateAlarm(alarm) }
    }

    fun delete(alarm: Alarm?) {
        alarm?.let { viewModelScope.launch { deleteAlarm(it) } }
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