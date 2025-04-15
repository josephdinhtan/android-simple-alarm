package com.jddev.simplealarm.domain.system

interface SystemSettingsManager {
    fun getMaxAlarmVolume(): Int
    fun getAlarmVolume(): Int
    fun setAlarmVolume(volume: Int)

    fun isDoNotDisturbEnabled(): Boolean
}