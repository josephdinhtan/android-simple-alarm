package com.jscoding.simplealarm.domain.platform

import com.jscoding.simplealarm.domain.model.DayOfWeek

interface AlarmScheduler {
    fun schedule(alarmId: Long, hour: Int, minute: Int)
    fun schedule(alarmId: Long, hour: Int, minute: Int, daysOfWeek: List<DayOfWeek>)
    fun cancel(alarmId: Long)
}