package com.jscoding.simplealarm.presentation.widget.model

import com.jscoding.simplealarm.domain.entity.alarm.DayOfWeek

data class AlarmWidgetModel (
    val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val label: String,
    val repeatDays: List<DayOfWeek>,
    val enabled: Boolean,
)