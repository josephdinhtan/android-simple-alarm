package com.jddev.simplealarm.domain.system

import com.jddev.simplealarm.domain.model.Alarm

interface NotificationController {
    fun showAlarmNotification(alarm: Alarm)
    fun cancelAlarmNotification(alarmId: Int)
    fun createNotificationChannels()
    fun isNotificationPermissionGranted(): Boolean
}