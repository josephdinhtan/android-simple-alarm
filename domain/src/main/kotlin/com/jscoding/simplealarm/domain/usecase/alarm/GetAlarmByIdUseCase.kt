package com.jscoding.simplealarm.domain.usecase.alarm

import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.repository.AlarmRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAlarmByIdUseCase @Inject constructor(
    private val repository: AlarmRepository,
) {
    suspend operator fun invoke(alarmId: Long): Alarm? {
        val result = repository.getAlarmById(alarmId)
        return result.getOrNull()
    }
}