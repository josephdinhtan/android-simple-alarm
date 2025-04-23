package com.jscoding.simplealarm.domain.platform

import com.jscoding.simplealarm.domain.entity.alarm.NotificationAction
import com.jscoding.simplealarm.domain.entity.alarm.NotificationType

interface NotificationController {
    fun cancelNotification(notificationId: Int)

    suspend fun showNotification(
        notificationId: Int,
        alarmId: Long,
        alarmLabel: String,
        hour: Int,
        minute: Int,
        type: NotificationType,
        actions: List<NotificationAction>,
    )
}