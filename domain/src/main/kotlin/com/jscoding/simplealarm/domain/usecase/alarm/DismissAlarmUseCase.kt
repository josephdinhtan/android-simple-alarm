package com.jscoding.simplealarm.domain.usecase.alarm

import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.platform.AlarmPreNotificationScheduler
import com.jscoding.simplealarm.domain.platform.AlarmRingingController
import com.jscoding.simplealarm.domain.platform.AlarmScheduler
import com.jscoding.simplealarm.domain.repository.AlarmRepository
import com.jscoding.simplealarm.domain.usecase.others.CancelNotificationUseCase
import com.jscoding.simplealarm.domain.utils.getNextTriggerTime
import com.jscoding.simplealarm.domain.utils.getPreAlarmNotificationTime
import java.time.LocalDateTime
import javax.inject.Inject

class DismissAlarmUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler,
    private val preNotificationScheduler: AlarmPreNotificationScheduler,
    private val alarmRingingController: AlarmRingingController,
    private val cancelNotificationUseCase: CancelNotificationUseCase,
) {
    suspend operator fun invoke(alarm: Alarm) {
        cancelNotificationUseCase(alarm)
        alarmRingingController.dismissRinging(alarm)
        alarmScheduler.cancel(alarm)
        if(alarm.repeatDays.isEmpty()) {
            alarmRepository.updateAlarm(alarm.copy(enabled = false))
        } else {
            val nextAlarmTrigger = alarm.getNextTriggerTime(LocalDateTime.now())
            alarmScheduler.schedule(alarm, nextAlarmTrigger)

            alarm.getPreAlarmNotificationTime(LocalDateTime.now())?.let {
                preNotificationScheduler.schedule(alarm, it)
            }
        }
    }
}