package com.jscoding.simplealarm.presentation.widget

import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.presentation.widget.model.AlarmWidgetModel

fun Alarm.toAlarmWidgetModel() = AlarmWidgetModel(
    id = this.id,
    label = this.label,
    hour = this.hour,
    minute = this.minute,
    repeatDays = this.repeatDays,
    enabled = this.enabled
)