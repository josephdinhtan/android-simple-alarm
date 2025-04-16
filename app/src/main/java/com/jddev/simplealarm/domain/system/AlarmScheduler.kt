package com.jddev.simplealarm.domain.system

import com.jddev.simplealarm.domain.model.alarm.Alarm

interface AlarmScheduler {
    fun schedule(alarm: Alarm)
    fun cancel(alarm: Alarm)
}