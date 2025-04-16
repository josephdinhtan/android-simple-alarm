package com.jddev.simplealarm.domain.system

import com.jddev.simplealarm.domain.model.alarm.AlarmTone

interface SystemSettingsManager {
    fun getDefaultAlarmTone(): AlarmTone?
    fun getMaxAlarmVolume(): Int
    fun getAlarmVolume(): Int
    fun setAlarmVolume(volume: Int)
    fun isDoNotDisturbEnabled(): Boolean
    fun getAlarmTones(): List<AlarmTone>
}