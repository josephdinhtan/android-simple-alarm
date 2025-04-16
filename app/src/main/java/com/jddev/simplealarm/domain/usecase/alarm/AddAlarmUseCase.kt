package com.jddev.simplealarm.domain.usecase.alarm

import com.jddev.simplealarm.domain.model.alarm.Alarm
import com.jddev.simplealarm.domain.repository.AlarmRepository
import com.jddev.simplealarm.domain.system.AlarmScheduler
import com.jddev.simplealarm.domain.system.NotificationController
import com.jddev.simplealarm.domain.usecase.SuspendUseCase
import java.time.Duration
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddAlarmUseCase @Inject constructor(
    private val repository: AlarmRepository,
    private val notificationController: NotificationController,
    private val alarmScheduler: AlarmScheduler,
) : SuspendUseCase<Alarm, Long> {
    override suspend operator fun invoke(params: Alarm): Long {
        val alarmId = repository.insertAlarm(params)
        if (params.isEnabled) {
            alarmScheduler.schedule(params.copy(id = alarmId))

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
        }
        return alarmId
    }
}