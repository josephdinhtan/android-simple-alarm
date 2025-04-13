package com.jddev.simplealarm.domain.model

import java.time.DayOfWeek

data class Alarm(
    val id: Int = 0,
    val hour: Int,
    val minute: Int,
    val label: String = "",
    val repeatDays: List<DayOfWeek> = emptyList(),
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)