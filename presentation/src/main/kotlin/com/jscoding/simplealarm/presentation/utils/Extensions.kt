package com.jscoding.simplealarm.presentation.utils

import com.jscoding.simplealarm.domain.model.alarm.Alarm
import com.jscoding.simplealarm.domain.model.alarm.Ringtone
import kotlin.time.Duration.Companion.minutes

fun Alarm.toStringTimeDisplay(is24HourFormat: Boolean): String {
    return when (is24HourFormat) {
        true -> "${hour.toString().padStart(2, '0')}:${
            minute.toString().padStart(2, '0')
        }"

        false -> {
            val period = if (hour < 12) "AM" else "PM"
            val hour12 = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            val minutesStr = minute.toString().padStart(2, '0')
            "$hour12:$minutesStr $period"
        }
    }
}

fun Alarm.Companion.default(): Alarm {
    return Alarm(
        id = 0L,
        hour = 6,
        minute = 0,
        label = "",
        ringtone = Ringtone.Silent,
        repeatDays = emptyList(),
        enabled = true,
        vibration = true,
        preAlarmNotificationDuration = 5.minutes,
        createdAt = System.currentTimeMillis(),
    )
}