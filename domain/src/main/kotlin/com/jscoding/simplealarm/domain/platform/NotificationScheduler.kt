package com.jscoding.simplealarm.domain.platform

import com.jscoding.simplealarm.domain.entity.alarm.DayOfWeek

interface NotificationScheduler {
    fun schedule(alarmId: Long, hour: Int, minute: Int, is24HourFormat: Boolean)
    fun schedule(alarmId: Long, hour: Int, minute: Int, daysOfWeek: List<DayOfWeek>, is24HourFormat: Boolean)
    fun cancel(alarmId: Long, hour: Int, minute: Int, is24HourFormat: Boolean)
    fun isScheduleNotificationAllowed(): Boolean
}