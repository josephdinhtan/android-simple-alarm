package com.jscoding.simplealarm.domain.usecase.alarm

import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.entity.exceptions.AlarmAlreadyExistsException
import com.jscoding.simplealarm.domain.platform.AlarmScheduler
import com.jscoding.simplealarm.domain.platform.NotificationScheduler
import com.jscoding.simplealarm.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddAlarmUseCase @Inject constructor(
    private val repository: AlarmRepository,
    private val notificationController: NotificationScheduler,
    private val alarmScheduler: AlarmScheduler,
) {
    suspend operator fun invoke(alarm: Alarm): Result<Unit> {
        val alarms = repository.getAllAlarms().firstOrNull()
        if (alarms != null && alarms.any { it.isSameAlarm(alarm) }) {
            return Result.failure(AlarmAlreadyExistsException())
        }
        val alarmId = repository.insertAlarm(alarm)
        if (alarm.enabled) {
            // schedule alarm
            if (alarm.repeatDays.isEmpty()) {
                alarmScheduler.schedule(alarmId, alarm.hour, alarm.minute)
            } else {
                alarmScheduler.schedule(alarmId, alarm.hour, alarm.minute, alarm.repeatDays)
            }

            // schedule pre-alarm notification
//            val alarmTime = LocalTime.of(params.hour, params.minute)
//            if (params.preAlarmNotificationDuration > Duration.ZERO &&
//                notificationController.isNotificationPermissionGranted() &&
//                alarmTime.isAfter(LocalTime.now())
//            ) {
//                notificationController.schedulePreAlarmNotification(
//                    params,
//                    params.preAlarmNotificationDuration
//                )
//            }
        }
        return Result.success(Unit)
    }

    private fun Alarm.isSameAlarm(other: Alarm): Boolean {
        return this.hour == other.hour && this.minute == other.minute && this.repeatDays.toSet() == other.repeatDays.toSet() && this.label == other.label
    }
}