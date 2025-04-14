package com.jddev.simplealarm.domain.usecase.alarm

import com.jddev.simplealarm.domain.model.Alarm
import com.jddev.simplealarm.domain.repository.AlarmRepository
import com.jddev.simplealarm.domain.scheduler.AlarmScheduler
import com.jddev.simplealarm.domain.usecase.SuspendUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteAlarmUseCase @Inject constructor(
    private val repository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler
) : SuspendUseCase<Alarm, Unit> {
    override suspend operator fun invoke(params: Alarm) {
        repository.deleteAlarm(params)
        alarmScheduler.cancel(params)
    }
}