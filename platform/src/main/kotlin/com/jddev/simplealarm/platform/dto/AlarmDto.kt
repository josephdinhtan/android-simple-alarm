package com.jddev.simplealarm.platform.dto

import kotlinx.serialization.Serializable

@Serializable
data class AlarmDto(
    val id: Long,
    val hour: Int,
    val minute: Int,
    val label: String,
    val ringtoneTitle: String,
    val ringtoneUri: String,
    val vibration: Boolean,
    val repeatDays: List<Int>, // DayOfWeek.ordinal
    val snoozeTimeMillis: Long,
    val preAlarmNotificationMillis: Long,
)