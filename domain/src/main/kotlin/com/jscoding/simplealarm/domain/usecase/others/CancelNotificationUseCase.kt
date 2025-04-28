package com.jscoding.simplealarm.domain.usecase.others

import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.platform.AlarmNotificationController
import javax.inject.Inject

class CancelNotificationUseCase @Inject constructor(
    private val notificationController: AlarmNotificationController,
) {
    operator fun invoke(alarm: Alarm) {
        notificationController.cancelAlarmNotification(alarm)
    }
}