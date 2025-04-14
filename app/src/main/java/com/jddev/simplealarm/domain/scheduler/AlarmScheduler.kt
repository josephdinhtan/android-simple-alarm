package com.jddev.simplealarm.domain.scheduler

import com.jddev.simplealarm.domain.model.Alarm

interface AlarmScheduler {
    fun schedule(alarm: Alarm)
    fun cancel(alarm: Alarm)
}