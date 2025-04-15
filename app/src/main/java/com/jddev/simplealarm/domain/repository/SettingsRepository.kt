package com.jddev.simplealarm.domain.repository

import android.net.Uri
import com.jddev.simplealarm.domain.model.settings.ThemeMode
import kotlinx.coroutines.flow.Flow
import java.time.Duration

interface SettingsRepository {

    val is24HourFormat: Flow<Boolean>
    val defaultRingtoneUri: Flow<Uri>
    val alarmVolume: Flow<Int>
    val isVibrationEnabled: Flow<Boolean>
    val snoozeDuration: Flow<Duration>
    val defaultLabel: Flow<String>
    val autoDismissTime: Flow<Duration>
    val gradualIncreaseVolume: Flow<Duration>
    val themeSetting: Flow<ThemeMode>
    val isUseDynamicColors: Flow<Boolean>
    val isFirstTime: Flow<Boolean>

    suspend fun set24HourFormat(enabled: Boolean)
    suspend fun setDefaultRingtoneUri(uri: Uri)
    suspend fun setAlarmVolume(volume: Int)
    suspend fun setVibrationEnabled(enabled: Boolean)
    suspend fun setSnoozeDuration(duration: Duration)
    suspend fun setDefaultLabel(label: String)
    suspend fun setAutoDismissTime(duration: Duration)
    suspend fun setGradualIncreaseVolume(duration: Duration)
    suspend fun setThemeSetting(themeMode: ThemeMode)
    suspend fun setUseDynamicColors(enabled: Boolean)
}