package com.jscoding.simplealarm.domain.usecase.alarm

import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.platform.AlarmPreNotificationScheduler
import com.jscoding.simplealarm.domain.platform.AlarmScheduler
import com.jscoding.simplealarm.domain.utils.getNextTriggerTime
import com.jscoding.simplealarm.domain.utils.getPreAlarmNotificationTime
import java.time.LocalDateTime
import javax.inject.Inject

class CancelScheduleAlarmUseCase @Inject constructor(
    private val alarmScheduler: AlarmScheduler,
    private val preNotificationScheduler: AlarmPreNotificationScheduler,
) {
    operator fun invoke(alarm: Alarm) {
        alarmScheduler.cancel(alarm)
        preNotificationScheduler.cancel(alarm)
    }
}