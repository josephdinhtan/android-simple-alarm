package com.jscoding.simplealarm.data.utils

import com.jscoding.simplealarm.domain.entity.alarm.DayOfWeek
import java.time.DateTimeException


fun DayOfWeek.Companion.of(dayOfWeek: Int): DayOfWeek {
    if (dayOfWeek < 1 || dayOfWeek > 7) {
        throw DateTimeException("Invalid value for DayOfWeek: $dayOfWeek")
    }
    return DayOfWeek.entries[dayOfWeek - 1]
}
