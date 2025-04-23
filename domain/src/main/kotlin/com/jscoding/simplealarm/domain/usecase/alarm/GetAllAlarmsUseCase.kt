package com.jscoding.simplealarm.domain.usecase.alarm

import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAllAlarmsUseCase @Inject internal constructor(
    private val repository: AlarmRepository,
) {
    operator fun invoke(): Flow<List<Alarm>> {
        return repository.getAllAlarms()
    }
}