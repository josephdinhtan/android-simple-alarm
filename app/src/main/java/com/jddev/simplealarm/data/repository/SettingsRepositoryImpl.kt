package com.jddev.simplealarm.data.repository

import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.jddev.simplealarm.domain.model.alarm.Ringtone
import com.jddev.simplealarm.domain.model.settings.ThemeMode
import com.jddev.simplealarm.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class SettingsRepositoryImpl @Inject constructor(
    private val dataStorePreferences: DataStore<Preferences>,
) : SettingsRepository {
    override val is24HourFormat: Flow<Boolean> = dataStorePreferences.data.map { preferences ->
        preferences[SettingsPreferencesKeys.is24HourFormat] ?: true
    }
    override val defaultRingtone: Flow<Ringtone> = dataStorePreferences.data.map { preferences ->
        val defaultRingtoneUriStr = preferences[SettingsPreferencesKeys.defaultRingtoneUri]
        val defaultRingtoneTitle = preferences[SettingsPreferencesKeys.defaultRingtoneTitle]
        if (defaultRingtoneUriStr != null && defaultRingtoneTitle != null) {
            Ringtone(defaultRingtoneTitle, Uri.parse(defaultRingtoneUriStr))
        } else {
            Ringtone.Silent
        }
    }
    override val defaultPreAlarmNotificationDuration: Flow<Duration> =
        dataStorePreferences.data.map { preferences ->
            val durationMinutes =
                preferences[SettingsPreferencesKeys.defaultPreAlarmNotificationMin] ?: 5
            durationMinutes.minutes
        }
    override val alarmVolume: Flow<Float> = dataStorePreferences.data.map { preferences ->
        preferences[SettingsPreferencesKeys.alarmVolume] ?: 1f
    }
    override val isVibrationEnabled: Flow<Boolean> = dataStorePreferences.data.map { preferences ->
        preferences[SettingsPreferencesKeys.isVibrationEnabled] ?: true
    }
    override val snoozeDuration: Flow<Duration> = dataStorePreferences.data.map { preferences ->
        val durationMinutes = preferences[SettingsPreferencesKeys.snoozeDuration] ?: 10
        durationMinutes.minutes
    }
    override val defaultLabel: Flow<String> = dataStorePreferences.data.map { preferences ->
        preferences[SettingsPreferencesKeys.defaultLabel] ?: "Alarm"
    }
    override val autoDismissTime: Flow<Duration> = dataStorePreferences.data.map { preferences ->
        val durationMinutes = preferences[SettingsPreferencesKeys.autoDismissTime] ?: 5
        durationMinutes.minutes
    }
    override val volumeFadeDuration: Flow<Duration> =
        dataStorePreferences.data.map { preferences ->
            val durationSeconds = preferences[SettingsPreferencesKeys.volumeFadeDuration] ?: 10
            durationSeconds.seconds
        }

    override val themeSetting: Flow<ThemeMode> = dataStorePreferences.data.map { preferences ->
        val themeModePref = preferences[SettingsPreferencesKeys.themeStyle]
        ThemeMode.entries.firstOrNull { it.name == themeModePref } ?: ThemeMode.SYSTEM
    }

    override val isUseDynamicColors: Flow<Boolean> = dataStorePreferences.data.map { preferences ->
        preferences[SettingsPreferencesKeys.useDynamicColors] ?: true
    }

    override suspend fun getDefaultPreAlarmNotificationDuration(): Duration {
        val durationMinutes =
            dataStorePreferences.data.first()[SettingsPreferencesKeys.defaultPreAlarmNotificationMin]
                ?: 5
        return durationMinutes.minutes
    }

    override suspend fun getSnoozeDuration(): Duration {
        val durationMinutes =
            dataStorePreferences.data.first()[SettingsPreferencesKeys.snoozeDuration] ?: 5
        return durationMinutes.minutes
    }

    override suspend fun getAutoDismissTime(): Duration {
        val durationMinutes =
            dataStorePreferences.data.first()[SettingsPreferencesKeys.autoDismissTime] ?: 5
        return durationMinutes.minutes
    }

    override suspend fun getAlarmVolume(): Float {
        return dataStorePreferences.data.first()[SettingsPreferencesKeys.alarmVolume] ?: 1f
    }

    override suspend fun getVolumeFadeDuration(): Duration {
        val durationSeconds =
            dataStorePreferences.data.first()[SettingsPreferencesKeys.volumeFadeDuration] ?: 0
        return durationSeconds.seconds
    }

    override suspend fun getIsFirstTimeStart(): Boolean {
        return dataStorePreferences.data.first()[SettingsPreferencesKeys.isFirstTime] ?: true
    }

    override suspend fun getDefaultRingtone(): Ringtone {
        val defaultRingtoneUriStr =
            dataStorePreferences.data.first()[SettingsPreferencesKeys.defaultRingtoneUri]
        val defaultRingtoneTitle =
            dataStorePreferences.data.first()[SettingsPreferencesKeys.defaultRingtoneTitle]
        return if (defaultRingtoneUriStr != null && defaultRingtoneTitle != null) {
            Ringtone(defaultRingtoneTitle, Uri.parse(defaultRingtoneUriStr))
        } else {
            Ringtone.Silent
        }
    }

    override suspend fun set24HourFormat(enabled: Boolean) {
        dataStorePreferences.edit { preferences ->
            preferences[SettingsPreferencesKeys.is24HourFormat] = enabled
        }
    }

    override suspend fun setDefaultRingtone(ringtone: Ringtone) {
        dataStorePreferences.edit { preferences ->
            preferences[SettingsPreferencesKeys.defaultRingtoneUri] = ringtone.uri.toString()
            preferences[SettingsPreferencesKeys.defaultRingtoneTitle] = ringtone.title
        }
    }

    override suspend fun setDefaultPreAlarmNotificationDuration(duration: Duration) {
        dataStorePreferences.edit { preferences ->
            preferences[SettingsPreferencesKeys.defaultPreAlarmNotificationMin] =
                duration.inWholeMinutes.toInt()
        }
    }

    override suspend fun setAlarmVolume(volume: Float) {
        dataStorePreferences.edit { preferences ->
            preferences[SettingsPreferencesKeys.alarmVolume] = volume
        }
    }

    override suspend fun setVibrationEnabled(enabled: Boolean) {
        dataStorePreferences.edit { preferences ->
            preferences[SettingsPreferencesKeys.isVibrationEnabled] = enabled
        }
    }

    override suspend fun setSnoozeDuration(duration: Duration) {
        dataStorePreferences.edit { preferences ->
            preferences[SettingsPreferencesKeys.snoozeDuration] = duration.inWholeMinutes.toInt()
        }
    }

    override suspend fun setDefaultLabel(label: String) {
        dataStorePreferences.edit { preferences ->
            preferences[SettingsPreferencesKeys.defaultLabel] = label
        }
    }

    override suspend fun setAutoDismissTime(duration: Duration) {
        dataStorePreferences.edit { preferences ->
            preferences[SettingsPreferencesKeys.autoDismissTime] = duration.inWholeMinutes.toInt()
        }
    }

    override suspend fun setVolumeFadeDuration(duration: Duration) {
        dataStorePreferences.edit { preferences ->
            preferences[SettingsPreferencesKeys.volumeFadeDuration] =
                duration.inWholeSeconds.toInt()
        }
    }

    override suspend fun setThemeSetting(themeMode: ThemeMode) {
        dataStorePreferences.edit { preferences ->
            preferences[SettingsPreferencesKeys.themeStyle] = themeMode.name
        }
    }

    override suspend fun setUseDynamicColors(enabled: Boolean) {
        dataStorePreferences.edit { preferences ->
            preferences[SettingsPreferencesKeys.useDynamicColors] = enabled
        }
    }

    override suspend fun setIsFirstTimeStart(value: Boolean) {
        dataStorePreferences.edit { preferences ->
            preferences[SettingsPreferencesKeys.isFirstTime] = value
        }
    }
}

private object SettingsPreferencesKeys {
    val useDynamicColors = booleanPreferencesKey(name = "use_dynamic_colors")
    val is24HourFormat = booleanPreferencesKey("is_24_hour_format")
    val defaultRingtoneTitle = stringPreferencesKey("default_ringtone_title")
    val defaultRingtoneUri = stringPreferencesKey("default_ringtone_uri")
    val defaultPreAlarmNotificationMin = intPreferencesKey("default_pre_alarm_notification_min")
    val alarmVolume = floatPreferencesKey("alarm_volume")
    val isVibrationEnabled = booleanPreferencesKey("vibration_enabled")
    val snoozeDuration = intPreferencesKey("snooze_duration") // store in minutes or seconds
    val defaultLabel = stringPreferencesKey("default_label")
    val autoDismissTime = intPreferencesKey("auto_dismiss_time") // in seconds
    val volumeFadeDuration = intPreferencesKey("volume_fade_duration") // in seconds
    val themeStyle = stringPreferencesKey("theme_style")
    val isFirstTime = booleanPreferencesKey("is_first_time")
}