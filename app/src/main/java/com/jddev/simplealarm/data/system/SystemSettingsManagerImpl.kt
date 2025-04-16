package com.jddev.simplealarm.data.system

import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import android.media.RingtoneManager
import com.jddev.simplealarm.domain.model.alarm.AlarmTone
import com.jddev.simplealarm.domain.system.SystemSettingsManager
import javax.inject.Inject

class SystemSettingsManagerImpl @Inject constructor(
    private val context: Context,
    private val audioManager: AudioManager,
    private val notificationManager: NotificationManager,
    private val ringtoneManager: RingtoneManager,
) : SystemSettingsManager {
    override fun getDefaultAlarmTone(): AlarmTone? {
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val title = RingtoneManager.getRingtone(context, uri).getTitle(context)

        return if (uri != null && title != null) {
            AlarmTone(
                title = title,
                uri = uri
            )
        } else {
            null
        }
    }

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

    override fun getAlarmTones(): List<AlarmTone> {
        val list = mutableListOf<AlarmTone>()
        val cursor = ringtoneManager.cursor
        while (cursor.moveToNext()) {
            val title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
            val uri = ringtoneManager.getRingtoneUri(cursor.position)
            list.add(AlarmTone(title, uri))
        }
        return list
    }
}