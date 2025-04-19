package com.jscoding.simplealarm.domain.usecase.alarm

import com.jscoding.simplealarm.domain.model.alarm.Alarm
import com.jscoding.simplealarm.domain.repository.AlarmRepository
import com.jscoding.simplealarm.domain.usecase.UseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAllAlarmsUseCase @Inject internal constructor(
    private val repository: AlarmRepository
) : UseCase<Unit, Flow<List<Alarm>>> {
    override operator fun invoke(params: Unit): Flow<List<Alarm>> {
        return repository.getAllAlarms()
    }
}