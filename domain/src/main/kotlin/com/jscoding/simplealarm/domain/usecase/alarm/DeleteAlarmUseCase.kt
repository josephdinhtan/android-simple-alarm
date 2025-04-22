package com.jscoding.simplealarm.domain.usecase.alarm

import com.jscoding.simplealarm.domain.platform.AlarmScheduler
import com.jscoding.simplealarm.domain.platform.NotificationScheduler
import com.jscoding.simplealarm.domain.repository.AlarmRepository
import com.jscoding.simplealarm.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteAlarmUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler,
    private val settingsRepository: SettingsRepository,
    private val notificationController: NotificationScheduler,
) {
    suspend operator fun invoke(alarmId: Long) {
        alarmRepository.deleteAlarm(alarmId)
        alarmScheduler.cancel(alarmId)

        alarmRepository.getAlarmById(alarmId)?.let { alarm ->
            val is24HourFormat = settingsRepository.getIs24HourFormat()
            notificationController.cancel(alarm.id, alarm.hour, alarm.minute, is24HourFormat)
        }
    }
}