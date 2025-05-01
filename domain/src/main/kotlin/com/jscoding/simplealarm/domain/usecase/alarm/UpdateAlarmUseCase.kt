package com.jscoding.simplealarm.domain.usecase.alarm

import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.entity.exceptions.NotificationNotAllowException
import com.jscoding.simplealarm.domain.platform.AlarmPreNotificationScheduler
import com.jscoding.simplealarm.domain.platform.AlarmScheduler
import com.jscoding.simplealarm.domain.repository.AlarmRepository
import com.jscoding.simplealarm.domain.utils.getNextTriggerTime
import com.jscoding.simplealarm.domain.utils.getPreAlarmNotificationTime
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateAlarmUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler,
    private val preNotificationScheduler: AlarmPreNotificationScheduler,
    private val tryScheduleNextAlarmUseCase: TryScheduleNextAlarmUseCase,
) {
    suspend operator fun invoke(alarm: Alarm): Result<Unit> {
        if (!preNotificationScheduler.isScheduleNotificationAllowed()) {
            return Result.failure(NotificationNotAllowException())
        }

        val result = alarmRepository.updateAlarm(alarm)
        if (result.isFailure) {
            return Result.failure(result.exceptionOrNull() ?: Exception("Failed to update alarm"))
        }

        if (alarm.enabled) {
            // schedule alarm
            alarmScheduler.cancel(alarm)
            preNotificationScheduler.cancel(alarm)


        } else {
            alarmScheduler.cancel(alarm)
            preNotificationScheduler.cancel(alarm)
        }
        return Result.success(Unit)
    }
}