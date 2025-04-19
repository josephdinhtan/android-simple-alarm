package com.jscoding.simplealarm.domain.usecase.alarm

import com.jscoding.simplealarm.domain.model.alarm.Alarm
import com.jscoding.simplealarm.domain.repository.AlarmRepository
import com.jscoding.simplealarm.domain.platform.AlarmScheduler
import com.jscoding.simplealarm.domain.platform.NotificationScheduler
import com.jscoding.simplealarm.domain.usecase.SuspendUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteAlarmUseCase @Inject constructor(
    private val repository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler,
    private val notificationController: NotificationScheduler,
) : SuspendUseCase<Alarm, Unit> {
    override suspend operator fun invoke(params: Alarm) {
        repository.deleteAlarm(params)
        alarmScheduler.cancel(params.id)
        notificationController.cancel(params.id)
    }
}