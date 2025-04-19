package com.jddev.simplealarm.domain.usecase.alarm

import com.jddev.simplealarm.domain.model.alarm.Alarm
import com.jddev.simplealarm.domain.platform.NotificationScheduler
import com.jddev.simplealarm.domain.platform.AlarmScheduler
import com.jddev.simplealarm.domain.repository.AlarmRepository
import com.jddev.simplealarm.domain.usecase.SuspendUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateAlarmUseCase @Inject constructor(
    private val repository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler,
    private val notificationController: NotificationScheduler,
) : SuspendUseCase<Alarm, Unit> {
    override suspend operator fun invoke(params: Alarm) {
        repository.updateAlarm(params)
        if (params.enabled) {
            // schedule alarm
            alarmScheduler.cancel(params.id)
            notificationController.cancel(params.id)
            if (params.repeatDays.isEmpty()) {
                alarmScheduler.schedule(params.id, params.hour, params.minute)
            } else {
                alarmScheduler.schedule(params.id, params.hour, params.minute, params.repeatDays)
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
        } else {
            alarmScheduler.cancel(params.id)
            notificationController.cancel(params.id)
        }
    }
}