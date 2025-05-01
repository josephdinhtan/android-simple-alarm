package com.jscoding.simplealarm.domain.usecase.alarm

import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.platform.AlarmPreNotificationScheduler
import com.jscoding.simplealarm.domain.platform.AlarmScheduler
import com.jscoding.simplealarm.domain.utils.getNextTriggerTime
import com.jscoding.simplealarm.domain.utils.getPreAlarmNotificationTime
import java.time.LocalDateTime
import javax.inject.Inject

class TryScheduleNextAlarmUseCase @Inject constructor(
    private val alarmScheduler: AlarmScheduler,
    private val preNotificationScheduler: AlarmPreNotificationScheduler,
) {
    operator fun invoke(alarm: Alarm) {
        if (!alarm.enabled) return

        val nextAlarmTrigger = alarm.getNextTriggerTime(LocalDateTime.now())
        alarmScheduler.schedule(alarm, nextAlarmTrigger)

        alarm.getPreAlarmNotificationTime(LocalDateTime.now())?.let {
            preNotificationScheduler.schedule(alarm, it)
        }
    }
}