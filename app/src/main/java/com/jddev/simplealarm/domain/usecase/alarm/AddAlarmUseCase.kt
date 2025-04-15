package com.jddev.simplealarm.domain.usecase.alarm

import com.jddev.simplealarm.domain.model.Alarm
import com.jddev.simplealarm.domain.repository.AlarmRepository
import com.jddev.simplealarm.domain.system.AlarmScheduler
import com.jddev.simplealarm.domain.usecase.SuspendUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddAlarmUseCase @Inject constructor(
    private val repository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler
): SuspendUseCase<Alarm, Long> {
    override suspend operator fun invoke(params: Alarm): Long {
        val alarmId = repository.insertAlarm(params)
        if (params.isEnabled) {
            alarmScheduler.schedule(params.copy(id = alarmId))
        }
        return alarmId
    }
}