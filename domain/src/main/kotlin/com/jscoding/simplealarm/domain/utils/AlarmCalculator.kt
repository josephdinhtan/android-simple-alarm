package com.jscoding.simplealarm.domain.utils

import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.time.Duration

internal fun Alarm.getNextTriggerTime(
    now: LocalDateTime = LocalDateTime.now(),
): LocalDateTime {
    val today = now.toLocalDate()
    val targetTimeToday = LocalDateTime.of(today, LocalTime.of(hour, minute))

    return if (repeatDays.isEmpty()) {
        if (targetTimeToday > now) targetTimeToday else targetTimeToday.plusDays(1)
    } else {
        repeatDays
            .map { dayOfWeek ->
                var daysUntil = (dayOfWeek.value - now.dayOfWeek.value + 7) % 7
                if (daysUntil == 0 && targetTimeToday <= now) daysUntil = 7
                today.plusDays(daysUntil.toLong()).atTime(hour, minute)
            }
            .minByOrNull { it }!!
    }
}

internal fun Alarm.getPreAlarmNotificationTime(
    now: LocalDateTime = LocalDateTime.now(),
): LocalDateTime? {
    if (this.preAlarmNotificationDuration == Duration.ZERO) return null
    val preAlarmTime =
        this.getNextTriggerTime(now).minusSeconds(this.preAlarmNotificationDuration.inWholeSeconds)

    return if (preAlarmTime.isAfter(now)) {
        preAlarmTime
    } else {
        null // Don't schedule if it's already passed
    }
}