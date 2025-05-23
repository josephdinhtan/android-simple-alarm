package com.jscoding.simplealarm.domain.repository

import com.jscoding.simplealarm.domain.entity.alarm.Ringtone
import com.jscoding.simplealarm.domain.entity.settings.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

interface SettingsRepository {

    // Reactive streams
    val is24HourFormat: Flow<Boolean>
    val defaultRingtone: Flow<Ringtone>
    val defaultPreAlarmNotificationDuration: Flow<Duration>
    val isVibrationEnabled: Flow<Boolean>
    val snoozeDuration: Flow<Duration>
    val defaultLabel: Flow<String>
    val autoDismissTime: Flow<Duration>
    val volumeFadeDuration: Flow<Duration>
    val themeSetting: Flow<ThemeMode>
    val isUseDynamicColors: Flow<Boolean>
    val ringingTimeLimit: Flow<Duration>

    // One-time reads for domain use cases
    suspend fun getIs24HourFormat(): Boolean
    suspend fun getDefaultPreAlarmNotificationDuration(): Duration
    suspend fun getSnoozeDuration(): Duration
    suspend fun getAutoDismissTime(): Duration
    suspend fun getVolumeFadeDuration(): Duration
    suspend fun getIsFirstTimeStart(): Boolean
    suspend fun getDefaultRingtone(): Ringtone
    suspend fun getRingingTimeLimit(): Duration

    // Setters
    suspend fun set24HourFormat(enabled: Boolean)
    suspend fun setDefaultRingtone(ringtone: Ringtone)
    suspend fun setDefaultPreAlarmNotificationDuration(duration: Duration)
    suspend fun setVibrationEnabled(enabled: Boolean)
    suspend fun setSnoozeDuration(duration: Duration)
    suspend fun setDefaultLabel(label: String)
    suspend fun setAutoDismissTime(duration: Duration)
    suspend fun setVolumeFadeDuration(duration: Duration)
    suspend fun setThemeSetting(themeMode: ThemeMode)
    suspend fun setUseDynamicColors(enabled: Boolean)
    suspend fun setIsFirstTimeStart(value: Boolean)
    suspend fun setRingingTimeLimit(duration: Duration)
}