package com.jscoding.simplealarm.domain.platform

interface AlarmRingingController {
    fun dismissRinging(alarmId: Long)
    fun snoozeRinging(alarmId: Long)
}