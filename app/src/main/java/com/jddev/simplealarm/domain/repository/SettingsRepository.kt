package com.jddev.simplealarm.domain.repository

import android.net.Uri
import com.jddev.simplealarm.domain.model.ThemeMode
import java.time.Duration

interface SettingsRepository {

    suspend fun is24HourFormat(): Boolean
    suspend fun set24HourFormat(enabled: Boolean)

    suspend fun getDefaultRingtoneUri(): Uri
    suspend fun setDefaultRingtoneUri(uri: Uri)

    suspend fun getAlarmVolume(): Int
    suspend fun setAlarmVolume(volume: Int)

    suspend fun isVibrationEnabled(): Boolean
    suspend fun setVibrationEnabled(enabled: Boolean)

    suspend fun getSnoozeDuration(): Duration
    suspend fun setSnoozeDuration(duration: Duration)

    suspend fun getDefaultLabel(): String
    suspend fun setDefaultLabel(label: String)

    suspend fun getAutoDismissTime(): Duration
    suspend fun setAutoDismissTime(duration: Duration)

    suspend fun isGradualVolumeEnabled(): Boolean
    suspend fun setGradualVolumeEnabled(enabled: Boolean)

    suspend fun getThemeSetting(): ThemeMode
    suspend fun setThemeSetting(themeMode: ThemeMode)

    suspend fun isFirstTime(): Boolean
}