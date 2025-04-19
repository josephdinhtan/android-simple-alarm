package com.jddev.simplealarm.domain.usecase.alarm

import com.jddev.simplealarm.domain.model.alarm.Alarm
import com.jddev.simplealarm.domain.repository.AlarmRepository
import com.jddev.simplealarm.domain.platform.AlarmScheduler
import com.jddev.simplealarm.domain.platform.NotificationScheduler
import com.jddev.simplealarm.domain.usecase.SuspendUseCase
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