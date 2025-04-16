package com.jddev.simplealarm.domain.model

import java.time.DayOfWeek
import java.time.Duration

data class Alarm(
    val id: Long = 0L,
    val hour: Int,
    val minute: Int,
    val label: String = "",
    val repeatDays: List<DayOfWeek> = emptyList(),
    val isEnabled: Boolean = true,
    val preAlarmNotificationDuration: Duration = Duration.ZERO,
    val createdAt: Long = System.currentTimeMillis()
)