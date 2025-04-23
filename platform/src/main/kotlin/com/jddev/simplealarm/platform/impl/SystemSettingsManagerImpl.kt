package com.jddev.simplealarm.platform.impl

import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import android.media.RingtoneManager
import android.text.format.DateFormat
import com.jscoding.simplealarm.domain.entity.alarm.Ringtone
import com.jscoding.simplealarm.domain.platform.SystemSettingsManager
import javax.inject.Inject

class SystemSettingsManagerImpl @Inject constructor(
    private val context: Context,
    private val audioManager: AudioManager,
    private val notificationManager: NotificationManager,
    private val ringtoneManager: RingtoneManager,
) : SystemSettingsManager {

    override fun is24HourFormat(): Boolean {
        return DateFormat.is24HourFormat(context)
    }

    override fun getDefaultRingtone(): Ringtone? {
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val title = RingtoneManager.getRingtone(context, uri).getTitle(context)

        return if (uri != null && title != null) {
            Ringtone(
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

    override fun getRingtones(): List<Ringtone> {
        val list = mutableListOf<Ringtone>()
        val cursor = ringtoneManager.cursor
        while (cursor.moveToNext()) {
            val title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
            val uri = ringtoneManager.getRingtoneUri(cursor.position)
            list.add(Ringtone(title, uri))
        }
        return list
    }
}