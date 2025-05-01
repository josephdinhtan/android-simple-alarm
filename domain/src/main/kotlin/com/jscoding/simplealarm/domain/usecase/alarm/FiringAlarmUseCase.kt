package com.jscoding.simplealarm.domain.usecase.alarm

import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.platform.AlarmRingingController
import com.jscoding.simplealarm.domain.repository.SettingsRepository
import javax.inject.Inject

class FiringAlarmUseCase @Inject constructor(
    private val alarmRingingController: AlarmRingingController,
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(alarm: Alarm) {
        val is24HourFormat = settingsRepository.getIs24HourFormat()
        val volumeFadeDuration = settingsRepository.getVolumeFadeDuration()
        val ringingTimeLimit = settingsRepository.getRingingTimeLimit()
        alarmRingingController.startFiringAlarm(
            alarm,
            is24HourFormat,
            volumeFadeDuration,
            ringingTimeLimit
        )
    }
}