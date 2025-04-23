package com.jscoding.simplealarm.domain.usecase.alarm


import com.jscoding.simplealarm.domain.entity.alarm.NotificationAction
import com.jscoding.simplealarm.domain.entity.alarm.NotificationType
import com.jscoding.simplealarm.domain.platform.NotificationController
import com.jscoding.simplealarm.domain.repository.AlarmRepository
import javax.inject.Inject

class ShowNotificationUseCase @Inject constructor(
    private val notificationController: NotificationController,
    private val alarmRepository: AlarmRepository,
) {
    suspend operator fun invoke(
        notificationId: Int,
        alarmId: Long,
        hour: Int,
        minute: Int,
        type: NotificationType,
    ) {
        val alarm = alarmRepository.getAlarmById(alarmId) ?: return

        val actions = when (type) {
            NotificationType.ALARM_UPCOMING -> listOf(
                NotificationAction.DISMISS,
                NotificationAction.SNOOZE
            )

            NotificationType.ALARM_FIRING -> listOf(
                NotificationAction.DISMISS,
                NotificationAction.SNOOZE
            )

            NotificationType.ALARM_SNOOZE -> listOf(NotificationAction.SNOOZE)
            NotificationType.ALARM_MISSED -> emptyList()
        }

        notificationController.showNotification(
            notificationId = notificationId,
            alarmId = alarmId,
            alarmLabel = alarm.label,
            hour = hour,
            minute = minute,
            type = type,
            actions = actions,
        )
    }
}