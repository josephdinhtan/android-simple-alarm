package com.jddev.simplealarm.domain.system

import com.jddev.simplealarm.domain.model.alarm.Ringtone

interface SystemSettingsManager {
    fun is24HourFormat(): Boolean
    fun getDefaultRingtone(): Ringtone?
    fun getMaxAlarmVolume(): Int
    fun getAlarmVolume(): Int
    fun setAlarmVolume(volume: Int)
    fun isDoNotDisturbEnabled(): Boolean
    fun getRingtones(): List<Ringtone>
}