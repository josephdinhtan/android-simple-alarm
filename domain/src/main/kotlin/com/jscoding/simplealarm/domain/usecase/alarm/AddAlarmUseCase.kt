package com.jscoding.simplealarm.domain.usecase.alarm

import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.platform.AlarmScheduler
import com.jscoding.simplealarm.domain.platform.NotificationScheduler
import com.jscoding.simplealarm.domain.repository.AlarmRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddAlarmUseCase @Inject constructor(
    private val repository: AlarmRepository,
    private val notificationController: NotificationScheduler,
    private val alarmScheduler: AlarmScheduler,
) {
    suspend operator fun invoke(alarm: Alarm): Long {
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
        return alarmId
    }
}