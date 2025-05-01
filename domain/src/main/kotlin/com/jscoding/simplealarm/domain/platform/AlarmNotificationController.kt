package com.jscoding.simplealarm.domain.platform

import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.entity.alarm.NotificationAction
import com.jscoding.simplealarm.domain.entity.alarm.NotificationType

interface AlarmNotificationController {
    fun cancelAlarmNotification(alarm: Alarm)
    fun showAlarmNotification(
        title: String,
        alarm: Alarm,
        is24hFormat: Boolean,
        type: NotificationType,
        actions: List<NotificationAction>,
    )
    fun isNotificationAllowed(): Boolean
}