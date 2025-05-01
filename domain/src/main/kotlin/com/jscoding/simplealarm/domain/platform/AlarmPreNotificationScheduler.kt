package com.jscoding.simplealarm.domain.platform

import com.jscoding.simplealarm.domain.entity.alarm.Alarm

interface AlarmPreNotificationScheduler {
    fun schedule(alarm: Alarm)
    fun cancel(alarm: Alarm)
    fun isScheduleNotificationAllowed(): Boolean
}