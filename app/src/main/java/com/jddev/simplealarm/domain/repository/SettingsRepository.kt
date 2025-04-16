package com.jddev.simplealarm.domain.repository

import android.net.Uri
import com.jddev.simplealarm.domain.model.alarm.AlarmTone
import com.jddev.simplealarm.domain.model.settings.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

interface SettingsRepository {

    // Reactive streams
    val is24HourFormat: Flow<Boolean>
    val defaultRingtone: Flow<AlarmTone>
    val defaultPreAlarmNotificationDuration: Flow<Duration>
    val alarmVolume: Flow<Float>
    val isVibrationEnabled: Flow<Boolean>
    val snoozeDuration: Flow<Duration>
    val defaultLabel: Flow<String>
    val autoDismissTime: Flow<Duration>
    val volumeFadeDuration: Flow<Duration>
    val themeSetting: Flow<ThemeMode>
    val isUseDynamicColors: Flow<Boolean>
    val isFirstTime: Flow<Boolean>

    // One-time reads for domain use cases
    suspend fun getDefaultPreAlarmNotificationDuration(): Duration
    suspend fun getSnoozeDuration(): Duration
    suspend fun getAutoDismissTime(): Duration
    suspend fun getAlarmVolume(): Float
    suspend fun getVolumeFadeDuration(): Duration

    // Setters
    suspend fun set24HourFormat(enabled: Boolean)
    suspend fun setDefaultRingtoneUri(uri: Uri)
    suspend fun setDefaultPreAlarmNotificationDuration(duration: Duration)
    suspend fun setAlarmVolume(volume: Float)
    suspend fun setVibrationEnabled(enabled: Boolean)
    suspend fun setSnoozeDuration(duration: Duration)
    suspend fun setDefaultLabel(label: String)
    suspend fun setAutoDismissTime(duration: Duration)
    suspend fun setVolumeFadeDuration(duration: Duration)
    suspend fun setThemeSetting(themeMode: ThemeMode)
    suspend fun setUseDynamicColors(enabled: Boolean)
}