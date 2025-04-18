package com.jddev.simplealarm.presentation.screens.alarm.alarmdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jddev.simplealarm.core.default
import com.jddev.simplealarm.domain.model.alarm.Alarm
import com.jddev.simplealarm.domain.repository.SettingsRepository
import com.jddev.simplealarm.domain.usecase.alarm.AddAlarmUseCase
import com.jddev.simplealarm.domain.usecase.alarm.DeleteAlarmUseCase
import com.jddev.simplealarm.domain.usecase.alarm.GetAlarmByIdUseCase
import com.jddev.simplealarm.domain.usecase.alarm.UpdateAlarmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
class AlarmDetailScreenViewModel @Inject constructor(
    private val getAlarmByIdUseCase: GetAlarmByIdUseCase,
    private val settingsRepository: SettingsRepository,
    private val addAlarmUseCase: AddAlarmUseCase,
    private val deleteAlarmUseCase: DeleteAlarmUseCase,
    private val updateAlarmUseCase: UpdateAlarmUseCase
) : ViewModel() {

    private val _alarm = MutableStateFlow<Alarm>(Alarm.default())
    val alarm = _alarm.asStateFlow()

    fun setupNewAlarm() {
        val calendar = Calendar.getInstance()
        val hour24hrs: Int = calendar.get(Calendar.HOUR_OF_DAY)
        val minutes: Int = calendar.get(Calendar.MINUTE)
        viewModelScope.launch {
            val defaultRingtone = settingsRepository.getDefaultRingtone()
            _alarm.value = Alarm(
                hour = hour24hrs,
                minute = minutes,
                ringtone = defaultRingtone,
                label = "",
                repeatDays = emptyList(),
                vibration = true,
                enabled = true,
                preAlarmNotificationDuration = 5.minutes,
                createdAt = System.currentTimeMillis(),
            )
        }
    }

    fun editAlarm(alarmId: Long) {
        viewModelScope.launch {
            _alarm.value = getAlarmByIdUseCase(alarmId) ?: Alarm.default()
        }
    }

    fun addNewAlarm(alarm: Alarm) {
        viewModelScope.launch {
            addAlarmUseCase(alarm)
        }
    }

    fun deleteAlarm(alarm: Alarm?) {
        if (alarm == null) return
        viewModelScope.launch {
            deleteAlarmUseCase(alarm)
        }
    }

    fun updateAlarm(alarm: Alarm) {
        viewModelScope.launch {
            updateAlarmUseCase(alarm)
        }
    }

    fun onAlarmValueChange(alarm: Alarm) {
        _alarm.value = alarm
    }
}