package com.jscoding.simplealarm.data.utils

import com.jscoding.simplealarm.domain.model.DayOfWeek
import com.jscoding.simplealarm.domain.model.alarm.Alarm
import java.time.DateTimeException
import java.util.Calendar

fun DayOfWeek.toCalendarDayOfWeek() : Int {
    return if(this == DayOfWeek.SUNDAY) {
        Calendar.SUNDAY
    } else {
        this.value + 1
    }
}

fun DayOfWeek.Companion.of(dayOfWeek: Int): DayOfWeek {
    if (dayOfWeek < 1 || dayOfWeek > 7) {
        throw DateTimeException("Invalid value for DayOfWeek: $dayOfWeek")
    }
    return DayOfWeek.entries[dayOfWeek - 1]
}

fun calculateTriggerTime(hour: Int, minute: Int): Long {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)

        // If time has already passed for today, schedule for tomorrow
        if (before(Calendar.getInstance())) {
            add(Calendar.DAY_OF_YEAR, 1)
        }
    }
    return calendar.timeInMillis
}

fun Alarm.toStringNotification(is24HourFormat: Boolean): String {
    val timeStr = "${hour.toString().padStart(2, '0')}:${
        minute.toString().padStart(2, '0')
    }"
    val labelStr = if (label.isNotBlank()) {
        " - $label"
    } else ""
    return timeStr + labelStr
}

fun calculateNextTriggerTime(dayOfWeek: DayOfWeek, hour: Int, minute: Int): Long {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        set(Calendar.DAY_OF_WEEK, dayOfWeek.toCalendarDayOfWeek())
        if (timeInMillis <= System.currentTimeMillis()) {
            add(Calendar.WEEK_OF_YEAR, 1)
        }
    }
    return calendar.timeInMillis
}