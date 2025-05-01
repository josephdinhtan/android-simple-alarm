package com.jscoding.simplealarm.presentation.screens.ringing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.usecase.alarm.DismissAlarmUseCase
import com.jscoding.simplealarm.domain.usecase.alarm.SnoozeAlarmUseCase
import com.jscoding.simplealarm.presentation.utils.toStringTimeDisplay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlin.time.Duration

sealed interface AlarmRingingState {
    data object None : AlarmRingingState
    data object Dismissed : AlarmRingingState

    data class Snoozed(
        val snoozedTimeDisplay: String,
    ) : AlarmRingingState

    data class Ringing(
        val timeDisplay: String,
        val label: String,
    ) : AlarmRingingState
}

@HiltViewModel
class AlarmRingingViewmodel @Inject constructor(
    private val dismissAlarmUseCase: DismissAlarmUseCase,
    private val snoozeAlarmUseCase: SnoozeAlarmUseCase,
) : ViewModel() {

    private val _shouldFinish = MutableStateFlow(false)
    val shouldFinish = _shouldFinish.asStateFlow()

    private val _alarmRingingState = MutableStateFlow<AlarmRingingState>(AlarmRingingState.None)
    val alarmRingingState = _alarmRingingState.asStateFlow()

    private val _alarmState = MutableStateFlow<Alarm?>(null)
    val alarmState = _alarmState.asStateFlow()

    private var is24h = false

    fun setupAlarm(alarm: Alarm, is24HourFormat: Boolean) {
        is24h = is24HourFormat
        viewModelScope.launch {
            _alarmState.value = alarm
            alarmState.value?.let {
                _alarmRingingState.value = AlarmRingingState.Ringing(
                    timeDisplay = it.toStringTimeDisplay(is24HourFormat), label = it.label
                )
            }
        }
    }

    fun finish() {
        _shouldFinish.value = true
    }

    fun requestDismissAlarm() {
        _alarmRingingState.value = AlarmRingingState.Dismissed
    }

    fun requestSnoozeAlarm() {
        val targetAlarm = alarmState.value ?: return
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
        }
        _alarmRingingState.value = AlarmRingingState.Snoozed(
            snoozedTimeDisplay = getSnoozedAlarmTimeDisplay(
                hour = calendar.get(Calendar.HOUR_OF_DAY),
                minutes = calendar.get(Calendar.MINUTE),
                snoozeTime = targetAlarm.snoozeTime,
                is24HourFormat = is24h
            )
        )
    }

    fun onDismissAlarm() {
        val targetAlarm = alarmState.value ?: return
        viewModelScope.launch {
            dismissAlarmUseCase(targetAlarm)
        }
    }

    fun onSnoozeAlarm() {
        val targetAlarm = alarmState.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            snoozeAlarmUseCase(targetAlarm)
        }
    }

    private fun getSnoozedAlarmTimeDisplay(
        hour: Int,
        minutes: Int,
        snoozeTime: Duration,
        is24HourFormat: Boolean,
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