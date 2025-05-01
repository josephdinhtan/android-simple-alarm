package com.jscoding.simplealarm.domain.usecase.alarm

import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.platform.AlarmPreNotificationScheduler
import com.jscoding.simplealarm.domain.platform.AlarmScheduler
import com.jscoding.simplealarm.domain.repository.AlarmRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteAlarmUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler,
    private val notificationController: AlarmPreNotificationScheduler,
) {
    suspend operator fun invoke(alarm: Alarm): Result<Unit> {

        alarmRepository.deleteAlarm(alarm)
        alarmScheduler.cancel(alarm)
        notificationController.cancel(alarm)

        // TODO hard code success
        return Result.success(Unit)
    }
}