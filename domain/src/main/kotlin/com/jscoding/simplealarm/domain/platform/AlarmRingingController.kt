package com.jscoding.simplealarm.domain.platform

import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import kotlin.time.Duration

interface AlarmRingingController {
    fun startFiringAlarm(
        alarm: Alarm, is24h: Boolean, volumeFadeDuration: Duration,
        ringingTimeLimit: Duration,
    )
    fun dismissAlarm(alarm: Alarm)
    fun missedAlarm(alarm: Alarm)
    fun snoozeAlarm(alarm: Alarm)
}