package com.jscoding.simplealarm.domain.usecase.alarm

import com.jscoding.simplealarm.domain.model.alarm.Alarm
import com.jscoding.simplealarm.domain.repository.AlarmRepository
import com.jscoding.simplealarm.domain.usecase.SuspendUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAlarmByIdUseCase @Inject constructor(
    private val repository: AlarmRepository,
) : SuspendUseCase<Long, Alarm?> {
    override suspend operator fun invoke(params: Long): Alarm? {
        return repository.getAlarmById(params)
    }
}