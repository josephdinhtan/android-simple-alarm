package com.jscoding.simplealarm.domain.platform

import com.jscoding.simplealarm.domain.entity.alarm.Alarm

interface AlarmRingingController {
    fun dismissRinging(alarm: Alarm)
    fun snoozeRinging(alarm: Alarm)
}