package com.jscoding.simplealarm.domain.usecase.alarm

import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.entity.exceptions.NotificationNotAllowException
import com.jscoding.simplealarm.domain.platform.AlarmNotificationScheduler
import com.jscoding.simplealarm.domain.platform.AlarmScheduler
import com.jscoding.simplealarm.domain.repository.AlarmRepository
import com.jscoding.simplealarm.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration

@Singleton
class UpdateAlarmUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler,
    private val settingsRepository: SettingsRepository,
    private val notificationController: AlarmNotificationScheduler,
) {
    suspend operator fun invoke(alarm: Alarm): Result<Unit> {
        if (!notificationController.isScheduleNotificationAllowed()) {
            return Result.failure(NotificationNotAllowException())
        }

        val result = alarmRepository.updateAlarm(alarm)
        if (result.isFailure) {
            return Result.failure(result.exceptionOrNull() ?: Exception("Failed to update alarm"))
        }

        if (alarm.enabled) {
            // schedule alarm
            alarmScheduler.cancel(alarm)
            alarmScheduler.schedule(alarm)
            notificationController.cancel(alarm)

            if (alarm.preAlarmNotificationDuration != Duration.ZERO) {
                notificationController.schedule(alarm)
            }
        } else {
            alarmScheduler.cancel(alarm)
            notificationController.cancel(alarm)
        }
        return Result.success(Unit)
    }
}