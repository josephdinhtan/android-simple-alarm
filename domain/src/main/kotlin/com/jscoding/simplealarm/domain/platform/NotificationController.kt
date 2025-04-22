package com.jscoding.simplealarm.domain.platform

import kotlin.time.Duration

interface NotificationController {
    fun cancelNotification(notificationId: Int)
    suspend fun showSnoozedNotification(
        notificationId: Int,
        alarmId: Long,
        hour: Int,
        minute: Int,
        snoozeTime: Duration,
    )
}