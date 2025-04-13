package com.jddev.simplealarm.domain.usecase

import com.jddev.simplealarm.domain.model.Alarm
import com.jddev.simplealarm.domain.repository.AlarmRepository
import javax.inject.Inject

class UpdateAlarmUseCase @Inject constructor(private val repository: AlarmRepository) {
    suspend operator fun invoke(alarm: Alarm) {
        return repository.updateAlarm(alarm)
    }
}