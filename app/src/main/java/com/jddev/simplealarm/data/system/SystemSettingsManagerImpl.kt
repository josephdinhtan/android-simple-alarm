package com.jddev.simplealarm.data.system

import android.app.NotificationManager
import android.media.AudioManager
import com.jddev.simplealarm.domain.system.SystemSettingsManager
import javax.inject.Inject

class SystemSettingsManagerImpl @Inject constructor(
    private val audioManager: AudioManager,
    private val notificationManager: NotificationManager,
) : SystemSettingsManager {

    override fun getMaxAlarmVolume(): Int {
        return audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
    }

    override fun getAlarmVolume(): Int {
        return audioManager.getStreamVolume(AudioManager.STREAM_ALARM)
    }

    override fun setAlarmVolume(volume: Int) {
        val showUi = false
        val flag = if (showUi) AudioManager.FLAG_SHOW_UI else 0
        audioManager.setStreamVolume(
            AudioManager.STREAM_ALARM,
            volume.coerceIn(0, getMaxAlarmVolume()),
            flag
        )
    }

    override fun isDoNotDisturbEnabled(): Boolean {
        return notificationManager.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_ALL
    }

}