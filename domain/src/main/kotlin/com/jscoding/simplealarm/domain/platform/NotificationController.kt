package com.jscoding.simplealarm.domain.platform

import android.app.Notification

interface NotificationController {
    fun createNotification(title: String, content: String, alarmId: Long): Notification
}