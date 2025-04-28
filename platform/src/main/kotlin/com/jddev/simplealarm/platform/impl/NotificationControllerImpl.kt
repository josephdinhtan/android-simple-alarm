package com.jddev.simplealarm.platform.impl

import com.jddev.simplealarm.platform.helper.NotificationHelper
import com.jddev.simplealarm.platform.utils.getAlarmTimeDisplay
import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.entity.alarm.NotificationAction
import com.jscoding.simplealarm.domain.entity.alarm.NotificationType
import com.jscoding.simplealarm.domain.platform.AlarmNotificationController
import timber.log.Timber
import javax.inject.Inject

class NotificationControllerImpl @Inject constructor(
    private val notificationHelper: NotificationHelper,
) : AlarmNotificationController {

    override fun cancelAlarmNotification(alarm: Alarm) {
        val notificationId = alarm.id.toInt()
        Timber.d("Cancel notification alarm: ${alarm.label}, id: ${alarm.id}, notificationId: $notificationId")
        notificationHelper.cancelNotification(notificationId)
    }

    override fun showAlarmNotification(
        title: String,
        alarm: Alarm,
        is24HourFormat: Boolean,
        type: NotificationType,
        actions: List<NotificationAction>,
    ) {
        val notificationId = alarm.id.toInt()
        val notificationContent = getAlarmTimeDisplay(
            hour = alarm.hour,
            minutes = alarm.minute,
            is24HourFormat,
        )

        Timber.d("Show notification alarm: ${alarm.label}, id: ${alarm.id}, title: $title, content: $notificationContent")
        val notification = notificationHelper.createAlarmNotification(
            title,
            notificationContent,
            alarm.id,
            type,
            actions
        )
        // safe in background, no need withContext here
        notificationHelper.showNotification(
            notificationId,
            notification
        )
    }
}