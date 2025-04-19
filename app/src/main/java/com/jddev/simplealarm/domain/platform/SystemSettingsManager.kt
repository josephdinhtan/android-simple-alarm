package com.jddev.simplealarm.domain.platform

import com.jddev.simplealarm.domain.model.alarm.Ringtone

interface SystemSettingsManager {
    fun getMaxAlarmVolume(): Int
    fun getAlarmVolume(): Int
    fun setAlarmVolume(volume: Int)

    fun is24HourFormat(): Boolean
    fun isDoNotDisturbEnabled(): Boolean

    fun getDefaultRingtone(): Ringtone?
    fun getRingtones(): List<Ringtone>
}