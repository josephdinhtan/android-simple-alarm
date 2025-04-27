package com.jscoding.simplealarm.domain.platform

import com.jscoding.simplealarm.domain.entity.alarm.Alarm

interface AlarmScheduler {
    fun schedule(alarm: Alarm)
    fun cancel(alarm: Alarm)
}