package com.jddev.simplealarm.domain.platform

import com.jddev.simplealarm.domain.model.DayOfWeek

interface NotificationScheduler {
    fun schedule(alarmId: Long, hour: Int, minute: Int)
    fun schedule(alarmId: Long, hour: Int, minute: Int, daysOfWeek: List<DayOfWeek>)
    fun cancel(alarmId: Long)
    fun isNotificationAllowed(): Boolean
}