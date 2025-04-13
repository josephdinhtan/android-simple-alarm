package com.jddev.simplealarm.domain.usecase

import com.jddev.simplealarm.domain.model.Alarm
import com.jddev.simplealarm.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllAlarmsUseCase @Inject internal constructor(
    private val repository: AlarmRepository
) {
    operator fun invoke(): Flow<List<Alarm>> {
        return repository.getAllAlarms()
    }
}