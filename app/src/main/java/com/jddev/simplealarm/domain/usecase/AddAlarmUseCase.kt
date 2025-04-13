package com.jddev.simplealarm.domain.usecase

import com.jddev.simplealarm.domain.model.Alarm
import com.jddev.simplealarm.domain.repository.AlarmRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddAlarmUseCase @Inject constructor(private val repository: AlarmRepository) {
    suspend operator fun invoke(alarm: Alarm): Long {
        return repository.insertAlarm(alarm)
    }
}