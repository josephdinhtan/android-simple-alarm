package com.jddev.simplealarm.domain.usecase.alarm

import com.jddev.simplealarm.domain.model.Alarm
import com.jddev.simplealarm.domain.repository.AlarmRepository
import com.jddev.simplealarm.domain.system.AlarmScheduler
import com.jddev.simplealarm.domain.system.NotificationController
import com.jddev.simplealarm.domain.usecase.SuspendUseCase
import java.time.Duration
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateAlarmUseCase @Inject constructor(
    private val repository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler,
    private val notificationController: NotificationController,
) : SuspendUseCase<Alarm, Unit> {
    override suspend operator fun invoke(params: Alarm) {
        repository.updateAlarm(params)
        if (params.isEnabled) {
            alarmScheduler.cancel(params)
            alarmScheduler.schedule(params)

            val alarmTime = LocalTime.of(params.hour, params.minute)
            if (params.preAlarmNotificationDuration > Duration.ZERO &&
                notificationController.isNotificationPermissionGranted() &&
                alarmTime.isAfter(LocalTime.now())
            ) {
                notificationController.schedulePreAlarmNotification(
                    params,
                    params.preAlarmNotificationDuration
                )
            }
        } else {
            alarmScheduler.cancel(params)
        }
    }
}