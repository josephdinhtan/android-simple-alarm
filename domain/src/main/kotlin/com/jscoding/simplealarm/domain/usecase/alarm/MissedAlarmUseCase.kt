package com.jscoding.simplealarm.domain.usecase.alarm

import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.entity.alarm.NotificationType
import com.jscoding.simplealarm.domain.platform.AlarmRingingController
import com.jscoding.simplealarm.domain.repository.AlarmRepository
import com.jscoding.simplealarm.domain.usecase.others.CancelNotificationUseCase
import com.jscoding.simplealarm.domain.usecase.others.ShowNotificationUseCase
import javax.inject.Inject

/**
 * Missed an Alarm
 *
 * Triggered when the ringing alarm reached limited time
 */
class MissedAlarmUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val alarmRingingController: AlarmRingingController,
    private val cancelNotificationUseCase: CancelNotificationUseCase,
    private val tryScheduleNextAlarmUseCase: TryScheduleNextAlarmUseCase,
    private val showNotificationUseCase: ShowNotificationUseCase,
) {
    suspend operator fun invoke(alarm: Alarm) {
        // Stop ringtone and Ringing Activity
        alarmRingingController.missedAlarm(alarm)
        // Cancel current notification
        cancelNotificationUseCase(alarm)
        // Re-schedule next alarm
        if(alarm.repeatDays.isEmpty()) {
            alarmRepository.updateAlarm(alarm.copy(enabled = false))
        } else {
            tryScheduleNextAlarmUseCase(alarm)
        }
        // Show missed notification
        showNotificationUseCase(alarm, "Missed Alarm", NotificationType.ALARM_MISSED)
    }
}