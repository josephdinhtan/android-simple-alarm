package com.jscoding.simplealarm.presentation.screens.ringing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jscoding.simplealarm.domain.model.alarm.Alarm
import com.jscoding.simplealarm.domain.repository.SettingsRepository
import com.jscoding.simplealarm.domain.usecase.alarm.DismissAlarmUseCase
import com.jscoding.simplealarm.domain.usecase.alarm.GetAlarmByIdUseCase
import com.jscoding.simplealarm.domain.usecase.alarm.SnoozeAlarmUseCase
import com.jscoding.simplealarm.presentation.utils.toStringTimeDisplay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject
import kotlin.time.Duration

sealed interface AlarmKlaxonState {
    data object None : AlarmKlaxonState
    data object Dismissed : AlarmKlaxonState

    data class Snoozed(
        val snoozedTimeDisplay: String,
    ) : AlarmKlaxonState

    data class Ringing(
        val timeDisplay: String,
        val label: String,
    ) : AlarmKlaxonState
}

@HiltViewModel
class AlarmKlaxonViewmodel @Inject constructor(
    private val dismissAlarmUseCase: DismissAlarmUseCase,
    private val snoozeAlarmUseCase: SnoozeAlarmUseCase,
    private val getAlarmByIdUseCase: GetAlarmByIdUseCase,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _alarmKlaxonState = MutableStateFlow<AlarmKlaxonState>(AlarmKlaxonState.None)
    val alarmKlaxonState = _alarmKlaxonState.asStateFlow()

    private val _alarm = MutableStateFlow<Alarm?>(null)
    val alarm = _alarm.asStateFlow()

    fun setupAlarm(alarmId: Long) {
        viewModelScope.launch {
            _alarm.value = getAlarmByIdUseCase(alarmId)
            _alarm.value?.let {
                val is24HourFormat = settingsRepository.getIs24HourFormat()
                _alarmKlaxonState.value = AlarmKlaxonState.Ringing(
                    timeDisplay = it.toStringTimeDisplay(is24HourFormat), label = it.label
                )
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
            _alarmKlaxonState.value = AlarmKlaxonState.Snoozed(
                snoozedTimeDisplay = getSnoozedAlarmTimeDisplay(
                    hour = alarm.value!!.hour,
                    minutes = alarm.value!!.minute,
                    snoozeTime = alarm.value!!.snoozeTime,
                    is24HourFormat = settingsRepository.getIs24HourFormat()
                )
            )
        }
    }

    private fun getSnoozedAlarmTimeDisplay(
        hour: Int,
        minutes: Int,
        snoozeTime: Duration,
        is24HourFormat: Boolean
    ): String {
        val now = LocalDateTime.now()
        var baseTime = now.withHour(hour).withMinute(minutes).withSecond(0).withNano(0)

        if (baseTime.isBefore(now)) {
            baseTime = baseTime.plusDays(1)
        }

        val javaDuration = java.time.Duration.ofMillis(snoozeTime.inWholeMilliseconds)
        val snoozedTime = baseTime.plus(javaDuration)

        val dayOfWeek = snoozedTime.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        val timeFormatter = if (is24HourFormat) {
            DateTimeFormatter.ofPattern("HH:mm")
        } else {
            DateTimeFormatter.ofPattern("hh:mm a")
        }

        return "$dayOfWeek ${snoozedTime.format(timeFormatter)}"
    }
}