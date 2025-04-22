package com.jscoding.simplealarm.domain.usecase.alarm

import com.jscoding.simplealarm.domain.model.alarm.Alarm
import com.jscoding.simplealarm.domain.platform.AlarmScheduler
import com.jscoding.simplealarm.domain.platform.NotificationScheduler
import com.jscoding.simplealarm.domain.repository.AlarmRepository
import com.jscoding.simplealarm.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateAlarmUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler,
    private val settingsRepository: SettingsRepository,
    private val notificationController: NotificationScheduler,
) {
    suspend operator fun invoke(alarm: Alarm) {
        alarmRepository.updateAlarm(alarm)
        val is24HourFormat = settingsRepository.getIs24HourFormat()
        if (alarm.enabled) {
            // schedule alarm
            alarmScheduler.cancel(alarm.id)
            notificationController.cancel(
                alarm.id,
                alarm.hour,
                alarm.minute,
                is24HourFormat
            )
            if (alarm.repeatDays.isEmpty()) {
                alarmScheduler.schedule(alarm.id, alarm.hour, alarm.minute)
            } else {
                alarmScheduler.schedule(alarm.id, alarm.hour, alarm.minute, alarm.repeatDays)
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
            alarmScheduler.cancel(alarm.id)
            notificationController.cancel(
                alarm.id,
                alarm.hour,
                alarm.minute,
                is24HourFormat
            )
        }
    }
}