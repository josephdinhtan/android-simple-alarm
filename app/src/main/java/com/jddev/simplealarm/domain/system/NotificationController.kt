package com.jddev.simplealarm.domain.system

import com.jddev.simplealarm.domain.model.alarm.Alarm
import java.time.Duration

interface NotificationController {
    fun schedulePreAlarmNotification(alarm: Alarm, notifyBeforeAt: Duration)
    fun showAlarmNotification(alarm: Alarm)
    fun cancelAlarmNotification(alarmId: Int)
    fun createNotificationChannels()
    fun isNotificationPermissionGranted(): Boolean
}