package com.jddev.simplealarm.domain.usecase.alarm

import com.jddev.simplealarm.domain.model.Alarm
import com.jddev.simplealarm.domain.repository.AlarmRepository
import com.jddev.simplealarm.domain.system.AlarmScheduler
import com.jddev.simplealarm.domain.usecase.SuspendUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateAlarmUseCase @Inject constructor(
    private val repository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler
) : SuspendUseCase<Alarm, Unit> {
    override suspend operator fun invoke(params: Alarm) {
        repository.updateAlarm(params)
        if (params.isEnabled) {
            alarmScheduler.schedule(params)
        } else {
            alarmScheduler.cancel(params)
        }
    }
}