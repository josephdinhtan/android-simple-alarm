package com.jddev.simplealarm.data.system

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import com.jddev.simplealarm.data.helper.AlarmManagerHelper
import com.jddev.simplealarm.data.helper.ScheduleType
import com.jddev.simplealarm.data.utils.calculateTriggerTime
import com.jddev.simplealarm.domain.model.alarm.Alarm
import com.jddev.simplealarm.domain.system.AlarmScheduler
import com.jddev.simplealarm.domain.system.NotificationController
import java.time.Duration
import javax.inject.Inject

class NotificationControllerImpl @Inject constructor(
    private val notificationManager: NotificationManager,
    private val alarmManagerHelper: AlarmManagerHelper,
) : NotificationController {
    override fun schedulePreAlarmNotification(alarm: Alarm, notifyBeforeAt: Duration) {
        val triggerTime = calculateTriggerTime(alarm.hour, alarm.minute) - notifyBeforeAt.toMillis()
        alarmManagerHelper.schedule(
            alarm.copy(preAlarmNotificationDuration = notifyBeforeAt),
            triggerTime,
            ScheduleType.NOTIFICATION
        )
    }

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