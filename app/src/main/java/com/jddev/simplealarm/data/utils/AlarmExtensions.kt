package com.jddev.simplealarm.data.utils

import com.jddev.simplealarm.domain.model.alarm.Alarm
import java.util.Calendar

internal fun calculateTriggerTime(hour: Int, minute: Int): Long {
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