package com.jscoding.simplealarm.domain.usecase.alarm

import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.entity.exceptions.AlarmAlreadyExistsException
import com.jscoding.simplealarm.domain.platform.AlarmNotificationScheduler
import com.jscoding.simplealarm.domain.platform.AlarmScheduler
import com.jscoding.simplealarm.domain.repository.AlarmRepository
import com.jscoding.simplealarm.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration

@Singleton
class AddAlarmUseCase @Inject constructor(
    private val repository: AlarmRepository,
    private val settingsRepository: SettingsRepository,
    private val notificationController: AlarmNotificationScheduler,
    private val alarmScheduler: AlarmScheduler,
) {
    suspend operator fun invoke(alarm: Alarm): Result<Unit> {
        // Check whether alarm is duplicated or not, if already existed no adding new Alarm
        val alarms = repository.getAllAlarms().firstOrNull()
        if (alarms != null && alarms.any { it.isSameAlarm(alarm) }) {
            return Result.failure(AlarmAlreadyExistsException())
        }
        val alarmId = repository.insertAlarm(alarm)

        if (alarm.enabled) {
            val scheduleAlarm = alarm.copy(id = alarmId)
            alarmScheduler.schedule(scheduleAlarm)
            if(alarm.preAlarmNotificationDuration != Duration.ZERO) {
                val is24HourFormat = settingsRepository.getIs24HourFormat()
                notificationController.schedule(scheduleAlarm, is24HourFormat)
            }
        }
        return Result.success(Unit)
    }

    private fun Alarm.isSameAlarm(other: Alarm): Boolean {
        return this.hour == other.hour && this.minute == other.minute && this.repeatDays.toSet() == other.repeatDays.toSet() && this.label == other.label
    }
}