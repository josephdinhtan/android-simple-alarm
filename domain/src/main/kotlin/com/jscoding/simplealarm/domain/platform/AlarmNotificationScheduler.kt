package com.jscoding.simplealarm.domain.platform

import com.jscoding.simplealarm.domain.entity.alarm.Alarm

interface AlarmNotificationScheduler {
    fun schedule(alarm: Alarm)
    fun cancel(alarm: Alarm)
    fun isScheduleNotificationAllowed(): Boolean
}