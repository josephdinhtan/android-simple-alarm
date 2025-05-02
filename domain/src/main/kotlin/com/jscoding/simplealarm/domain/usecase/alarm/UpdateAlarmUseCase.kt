package com.jscoding.simplealarm.domain.usecase.alarm

import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.entity.exceptions.NotificationNotAllowException
import com.jscoding.simplealarm.domain.platform.AlarmNotificationController
import com.jscoding.simplealarm.domain.repository.AlarmRepository
import javax.inject.Inject

/**
 * Update an Alarm
 */
class UpdateAlarmUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val notificationController: AlarmNotificationController,
    private val cancelScheduleAlarmUseCase: CancelScheduleAlarmUseCase,
    private val tryScheduleNextAlarmUseCase: TryScheduleNextAlarmUseCase,
) {
    suspend operator fun invoke(oldAlarm: Alarm, newAlarm: Alarm): Result<Unit> {
        if (oldAlarm == newAlarm) {
            return Result.success(Unit)
        }
        if (oldAlarm.id != newAlarm.id) {
            return Result.failure(Exception("Alarm id cannot be changed"))
        }
        if (!notificationController.isNotificationAllowed()) {
            return Result.failure(NotificationNotAllowException())
        }

        val result = alarmRepository.updateAlarm(newAlarm)
        if (result.isFailure) {
            return Result.failure(
                result.exceptionOrNull() ?: Exception("Unknown failure during alarm update")
            )
        }

        // Alarm schedule handling
        val wasEnabled = oldAlarm.enabled
        val isEnabled = newAlarm.enabled

        val shouldReschedule = isEnabled && (
                !wasEnabled || // just enabled
                        oldAlarm.repeatDays.toSet() != newAlarm.repeatDays.toSet() ||
                        oldAlarm.hour != newAlarm.hour ||
                        oldAlarm.minute != newAlarm.minute
                )

        val shouldCancel = wasEnabled && (
                !isEnabled || // just disabled
                        oldAlarm.repeatDays.toSet() != newAlarm.repeatDays.toSet() ||
                        oldAlarm.hour != newAlarm.hour ||
                        oldAlarm.minute != newAlarm.minute
                )

        if (shouldCancel) {
            cancelScheduleAlarmUseCase(oldAlarm)
        }
        if (shouldReschedule) {
            tryScheduleNextAlarmUseCase(newAlarm)
        }
        return Result.success(Unit)
    }
}