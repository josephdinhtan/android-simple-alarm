package com.jscoding.simplealarm.domain.usecase.alarm

import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.entity.exceptions.AlarmAlreadyExistsException
import com.jscoding.simplealarm.domain.entity.exceptions.NotificationNotAllowException
import com.jscoding.simplealarm.domain.platform.AlarmPreNotificationScheduler
import com.jscoding.simplealarm.domain.platform.AlarmScheduler
import com.jscoding.simplealarm.domain.repository.AlarmRepository
import com.jscoding.simplealarm.domain.utils.getNextTriggerTime
import com.jscoding.simplealarm.domain.utils.getPreAlarmNotificationTime
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration

@Singleton
class AddAlarmUseCase @Inject constructor(
    private val repository: AlarmRepository,
    private val notificationController: AlarmPreNotificationScheduler,
    private val tryScheduleNextAlarmUseCase: TryScheduleNextAlarmUseCase,
) {
    suspend operator fun invoke(alarm: Alarm): Result<Unit> {
        if (!notificationController.isScheduleNotificationAllowed()) {
            return Result.failure(NotificationNotAllowException())
        }

        // Check whether alarm is duplicated or not, if already existed no adding new Alarm
        val alarms = repository.getAllAlarms().firstOrNull()
        if (alarms != null && alarms.any { it.isSameAlarm(alarm) }) {
            return Result.failure(AlarmAlreadyExistsException())
        }
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