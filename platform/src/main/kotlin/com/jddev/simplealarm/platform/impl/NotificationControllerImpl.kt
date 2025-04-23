package com.jddev.simplealarm.platform.impl

import com.jddev.simplealarm.platform.helper.NotificationHelper
import com.jddev.simplealarm.platform.utils.getAlarmTimeDisplay
import com.jddev.simplealarm.platform.utils.getSnoozedAlarmTimeDisplay
import com.jscoding.simplealarm.domain.entity.alarm.NotificationAction
import com.jscoding.simplealarm.domain.entity.alarm.NotificationType
import com.jscoding.simplealarm.domain.platform.NotificationController
import com.jscoding.simplealarm.domain.repository.SettingsRepository
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale
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

    override suspend fun showNotification(
        notificationId: Int,
        alarmId: Long,
        alarmLabel: String,
        hour: Int,
        minute: Int,
        type: NotificationType,
        actions: List<NotificationAction>,
    ) {
        Timber.d("Show notification id: $notificationId")
        val is24HourFormat = settingsRepository.getIs24HourFormat()
        val notificationTitle = when (type) {
            NotificationType.ALARM_UPCOMING -> {
                "Upcoming alarm"
            }

            NotificationType.ALARM_FIRING -> {
                "Alarm"
            }

            NotificationType.ALARM_SNOOZE -> {
                "Snoozed alarm"
            }

            NotificationType.ALARM_MISSED -> {
                "Missed alarm"
            }
        }
        val notificationContent = getAlarmTimeDisplay(
            hour = hour,
            minutes = minute,
            is24HourFormat,
        )

        Timber.d("Show notification id: ${notificationId}, title: $notificationTitle, content: $notificationContent")
        val notification = notificationHelper.createAlarmNotification(
            notificationTitle,
            notificationContent,
            notificationId.toLong(),
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