package com.jscoding.simplealarm.domain.usecase.others

import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.entity.alarm.NotificationAction
import com.jscoding.simplealarm.domain.entity.alarm.NotificationType
import com.jscoding.simplealarm.domain.platform.AlarmNotificationController
import com.jscoding.simplealarm.domain.repository.SettingsRepository
import javax.inject.Inject

class ShowNotificationUseCase @Inject constructor(
    private val notificationController: AlarmNotificationController,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(
        alarm: Alarm,
        title: String,
        type: NotificationType,
    ) {
        val actions = when (type) {
            NotificationType.ALARM_UPCOMING -> listOf(
                NotificationAction.DISMISS,
                NotificationAction.SNOOZE
            )

            NotificationType.ALARM_FIRING -> listOf(
                NotificationAction.DISMISS,
                NotificationAction.SNOOZE
            )

            NotificationType.ALARM_SNOOZE -> listOf(NotificationAction.DISMISS)
            NotificationType.ALARM_MISSED -> emptyList()
        }
        val is24hFormat = settingsRepository.getIs24HourFormat()
        notificationController.showAlarmNotification(title, alarm, is24hFormat, type, actions)
    }
}