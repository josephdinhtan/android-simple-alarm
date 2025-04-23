package com.jscoding.simplealarm.domain.entity.alarm

import kotlin.time.Duration

data class Alarm(
    val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val label: String,
    val ringtone: Ringtone,
    val vibration: Boolean,
    val repeatDays: List<DayOfWeek>,
    val enabled: Boolean,
    val snoozeTime: Duration,
    val preAlarmNotificationDuration: Duration,
    val createdAt: Long,
) {
    companion object
}