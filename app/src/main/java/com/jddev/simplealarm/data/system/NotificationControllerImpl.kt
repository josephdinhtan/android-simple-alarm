package com.jddev.simplealarm.data.system

import android.app.NotificationManager
import com.jddev.simplealarm.domain.model.Alarm
import com.jddev.simplealarm.domain.system.NotificationController
import javax.inject.Inject

class NotificationControllerImpl @Inject constructor(
    private val notificationManager: NotificationManager
) : NotificationController {

    override fun showAlarmNotification(alarm: Alarm) {
        TODO("Not yet implemented")
    }

    override fun cancelAlarmNotification(alarmId: Int) {
        TODO("Not yet implemented")
    }

    override fun createNotificationChannels() {
        TODO("Not yet implemented")
    }

    override fun isNotificationPermissionGranted(): Boolean {
        return notificationManager.areNotificationsEnabled()
    }
}