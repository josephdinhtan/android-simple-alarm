package com.jddev.simplealarm.impl

import com.jddev.simplealarm.helper.NotificationHelper
import com.jscoding.simplealarm.data.utils.getSnoozedAlarmTimeDisplay
import com.jscoding.simplealarm.domain.platform.NotificationController
import com.jscoding.simplealarm.domain.repository.SettingsRepository
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration

class NotificationControllerImpl @Inject constructor(
    private val notificationHelper: NotificationHelper,
    private val settingsRepository: SettingsRepository,
) : NotificationController {

    override fun cancelNotification(notificationId: Int) {
        Timber.d("Cancel notification id: $notificationId")
        notificationHelper.cancelNotification(notificationId)
    }

    override suspend fun showSnoozedNotification(
        notificationId: Int,
        alarmId: Long,
        alarmHour: Int,
        alarmMinute: Int,
        snoozeTime: Duration,
    ) {
        val is24HourFormat = settingsRepository.getIs24HourFormat()
        val notificationTitle = "Snoozed alarm"
        val notificationContent = getSnoozedAlarmTimeDisplay(
            hour = alarmHour,
            minutes = alarmMinute,
            snoozeTime = snoozeTime,
            is24HourFormat
        )
        Timber.d("Show notification id: ${notificationId}, title: $notificationTitle, content: $notificationContent")
        val notification = notificationHelper.createAlarmDismissNotification(
            notificationTitle,
            notificationContent,
            notificationId.toLong()
        )
        // safe in background, no need withContext here
        notificationHelper.showNotification(
            notificationId,
            notification
        )
    }
}