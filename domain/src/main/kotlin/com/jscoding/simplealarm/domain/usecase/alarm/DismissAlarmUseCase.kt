package com.jscoding.simplealarm.domain.usecase.alarm

import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.platform.AlarmRingingController
import com.jscoding.simplealarm.domain.repository.AlarmRepository
import com.jscoding.simplealarm.domain.usecase.others.CancelNotificationUseCase
import javax.inject.Inject

/**
 * Dismiss an Alarm
 *
 * Triggered when the user dismisses the alarm while ringing
 */
class DismissAlarmUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val alarmRingingController: AlarmRingingController,
    private val cancelNotificationUseCase: CancelNotificationUseCase,
    private val tryScheduleNextAlarmUseCase: TryScheduleNextAlarmUseCase
) {
    suspend operator fun invoke(alarm: Alarm) {
        // Cancel notification
        cancelNotificationUseCase(alarm)
        // Stop ringtone and Ringing Activity
        alarmRingingController.dismissAlarm(alarm)
        if(alarm.repeatDays.isEmpty()) {
            alarmRepository.updateAlarm(alarm.copy(enabled = false))
        } else {
            tryScheduleNextAlarmUseCase(alarm)
        }
    }
}