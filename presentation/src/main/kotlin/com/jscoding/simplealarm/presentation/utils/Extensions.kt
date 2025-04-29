package com.jscoding.simplealarm.presentation.utils

import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.entity.alarm.DayOfWeek
import com.jscoding.simplealarm.domain.entity.alarm.Ringtone
import com.jscoding.simplealarm.presentation.widget.model.AlarmWidgetModel
import kotlin.time.Duration.Companion.minutes

fun Alarm.toStringTimeDisplay(is24HourFormat: Boolean): String {
    return when (is24HourFormat) {
        true -> "${hour.toString().padStart(2, '0')}:${
            minute.toString().padStart(2, '0')
        }"

        false -> {
            val hour12 = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            val minutesStr = minute.toString().padStart(2, '0')
            "$hour12:$minutesStr"
        }
    }
}

fun AlarmWidgetModel.toStringTimeDisplay(is24HourFormat: Boolean): String {
    return when (is24HourFormat) {
        true -> "${hour.toString().padStart(2, '0')}:${
            minute.toString().padStart(2, '0')
        }"

        false -> {
            val hour12 = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            val minutesStr = minute.toString().padStart(2, '0')
            "$hour12:$minutesStr"
        }
    }
}

fun Alarm.toAmPmNotationStr(): String {
    return if (hour < 12) "AM" else "PM"
}

fun AlarmWidgetModel.toAmPmNotationStr(): String {
    return if (hour < 12) "AM" else "PM"
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
        snoozeTime = 5.minutes,
        preAlarmNotificationDuration = 5.minutes,
        createdAt = System.currentTimeMillis(),
    )
}

fun List<DayOfWeek>.toDisplayString(prefixStr: String = ""): String {
    return if (this.isEmpty()) {
        "Once, no repeat"
    } else {
        val sortedDays = this.sortedBy { it.value }
        var str = sortedDays.joinToString(", ") { it.name.take(3).toFirstCapital() }
        if (prefixStr.isNotEmpty()) {
            str = "$prefixStr $str"
        }
        str
    }
}

fun String.toFirstCapital(): String {
    return this.lowercase().replaceFirstChar { it.uppercase() }
}