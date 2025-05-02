package com.jscoding.simplealarm.domain.usecase.alarm

import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.entity.exceptions.AlarmAlreadyExistsException
import com.jscoding.simplealarm.domain.entity.exceptions.NotificationNotAllowException
import com.jscoding.simplealarm.domain.platform.AlarmNotificationController
import com.jscoding.simplealarm.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

/**
 * Add a new Alarm
 */
class AddAlarmUseCase @Inject constructor(
    private val repository: AlarmRepository,
    private val notificationController: AlarmNotificationController,
    private val tryScheduleNextAlarmUseCase: TryScheduleNextAlarmUseCase,
) {
    suspend operator fun invoke(alarm: Alarm): Result<Unit> {
        if (!notificationController.isNotificationAllowed()) {
            return Result.failure(NotificationNotAllowException())
        }

        // Check whether alarm is duplicated or not, if already existed no adding new Alarm
        val alarms = repository.getAllAlarms().firstOrNull()
        if (alarms != null && alarms.any { it.isSameAlarm(alarm) }) {
            return Result.failure(AlarmAlreadyExistsException())
        }

        // Add new alarm
        val result = repository.insertAlarm(alarm)
        val alarmId =
            result.getOrNull() ?: return Result.failure(Exception("Failed to insert alarm"))

        tryScheduleNextAlarmUseCase(alarm.copy(id = alarmId))
        return Result.success(Unit)
    }

    private fun Alarm.isSameAlarm(other: Alarm): Boolean {
        return this.hour == other.hour && this.minute == other.minute && this.repeatDays.toSet() == other.repeatDays.toSet() && this.label == other.label
    }
}