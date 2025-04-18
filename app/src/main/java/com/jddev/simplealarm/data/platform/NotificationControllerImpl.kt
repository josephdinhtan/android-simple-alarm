package com.jddev.simplealarm.data.platform

import android.content.Context
import androidx.work.WorkManager
import com.jddev.simplealarm.core.getTimeInMillis
import com.jddev.simplealarm.core.toStringNotification
import com.jddev.simplealarm.data.helper.AlarmManagerHelper
import com.jddev.simplealarm.data.helper.NotificationHelper
import com.jddev.simplealarm.data.worker.PreAlertWorker
import com.jddev.simplealarm.domain.model.alarm.Alarm
import com.jddev.simplealarm.domain.repository.SettingsRepository
import com.jddev.simplealarm.domain.system.NotificationController
import javax.inject.Inject
import kotlin.time.Duration

class NotificationControllerImpl @Inject constructor(
    private val notificationHelper: NotificationHelper,
    private val settingsRepository: SettingsRepository,
    private val context: Context
) : NotificationController {
    override fun schedulePreAlarmNotification(alarm: Alarm, notifyBeforeAt: Duration) {
        val alarmTime = alarm.getTimeInMillis()
        val preAlertTime = alarmTime - notifyBeforeAt.inWholeMilliseconds
        val delay = preAlertTime - System.currentTimeMillis()

        if (delay > 0) {
            val request = PreAlertWorker.buildRequest(alarm.id, delay)
            WorkManager.getInstance(context).enqueue(request)
        }
    }

    override suspend fun showAlarmNotification(alarm: Alarm) {
        val is24HourFormat = settingsRepository.getIs24HourFormat()
        notificationHelper.showAlarmAlertNotification(alarm.toStringNotification(is24HourFormat))
    }

    override fun cancelAlarmNotification(alarmId: Int) {
        TODO("Not yet implemented")
    }

    override fun createNotificationChannels() {
        TODO("Not yet implemented")
    }

    override fun isNotificationPermissionGranted(): Boolean {
        return true
    }
}