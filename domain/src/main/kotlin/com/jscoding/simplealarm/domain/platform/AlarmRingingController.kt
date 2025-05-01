package com.jscoding.simplealarm.domain.platform

import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import kotlin.time.Duration

interface AlarmRingingController {
    fun startRinging(alarm: Alarm, is24h: Boolean, volumeFadeDuration: Duration)
    fun dismissRinging(alarm: Alarm)
    fun snoozeRinging(alarm: Alarm)
}