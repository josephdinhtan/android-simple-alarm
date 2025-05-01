package com.jscoding.simplealarm.domain.platform

import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import java.time.LocalDateTime

interface AlarmPreNotificationScheduler {
    fun schedule(alarm: Alarm, triggerAt: LocalDateTime)
    fun cancel(alarm: Alarm)
    fun isScheduleNotificationAllowed(): Boolean
}