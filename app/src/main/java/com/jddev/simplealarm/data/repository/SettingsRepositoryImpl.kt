package com.jddev.simplealarm.data.repository

import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.jddev.simplealarm.domain.model.settings.ThemeMode
import com.jddev.simplealarm.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.Duration
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStorePreferences: DataStore<Preferences>,
) : SettingsRepository {
    override val is24HourFormat: Flow<Boolean> = dataStorePreferences.data.map { preferences ->
        preferences[SettingsPreferencesKeys.is24HourFormat] ?: true
    }
    override val defaultRingtoneUri: Flow<Uri> = dataStorePreferences.data.map { preferences ->
        val uriString = preferences[SettingsPreferencesKeys.defaultRingtoneUri]
        uriString?.let { Uri.parse(it) } ?: Uri.EMPTY
    }
    override val defaultPreAlarmNotificationDuration: Flow<Duration> =
        dataStorePreferences.data.map { preferences ->
            val durationMinutes =
                preferences[SettingsPreferencesKeys.defaultPreAlarmNotificationMin] ?: 5
            Duration.ofMinutes(durationMinutes.toLong())
        }
    override val alarmVolume: Flow<Int> = dataStorePreferences.data.map { preferences ->
        preferences[SettingsPreferencesKeys.alarmVolume] ?: 50
    }
    override val isVibrationEnabled: Flow<Boolean> = dataStorePreferences.data.map { preferences ->
        preferences[SettingsPreferencesKeys.isVibrationEnabled] ?: true
    }
    override val snoozeDuration: Flow<Duration> = dataStorePreferences.data.map { preferences ->
        val durationMinutes = preferences[SettingsPreferencesKeys.snoozeDuration] ?: 10
        Duration.ofMinutes(durationMinutes.toLong())
    }
    override val defaultLabel: Flow<String> = dataStorePreferences.data.map { preferences ->
        preferences[SettingsPreferencesKeys.defaultLabel] ?: "Alarm"
    }
    override val autoDismissTime: Flow<Duration> = dataStorePreferences.data.map { preferences ->
        val durationMinutes = preferences[SettingsPreferencesKeys.autoDismissTime] ?: 5
        Duration.ofMinutes(durationMinutes.toLong())
    }
    override val gradualIncreaseVolume: Flow<Duration> =
        dataStorePreferences.data.map { preferences ->
            val durationSeconds = preferences[SettingsPreferencesKeys.gradualVolumeIncrease] ?: 10
            Duration.ofSeconds(durationSeconds.toLong())
        }

    override val themeSetting: Flow<ThemeMode> = dataStorePreferences.data.map { preferences ->
        val themeModePref = preferences[SettingsPreferencesKeys.themeStyle]
        ThemeMode.entries.firstOrNull { it.name == themeModePref } ?: ThemeMode.SYSTEM
    }

    override val isUseDynamicColors: Flow<Boolean> = dataStorePreferences.data.map { preferences ->
        preferences[SettingsPreferencesKeys.useDynamicColors] ?: true
    }

    override val isFirstTime: Flow<Boolean> = dataStorePreferences.data.map { preferences ->
        preferences[SettingsPreferencesKeys.isFirstTime] ?: true
    }

    override suspend fun getDefaultPreAlarmNotificationDuration(): Duration {
        val durationMinutes =
            dataStorePreferences.data.first()[SettingsPreferencesKeys.defaultPreAlarmNotificationMin]
                ?: 5
        return Duration.ofMinutes(durationMinutes.toLong())
    }

    override suspend fun getSnoozeDuration(): Duration {
        val durationMinutes =
            dataStorePreferences.data.first()[SettingsPreferencesKeys.snoozeDuration] ?: 5
        return Duration.ofMinutes(durationMinutes.toLong())
    }

    override suspend fun getAutoDismissTime(): Duration {
        val durationMinutes =
            dataStorePreferences.data.first()[SettingsPreferencesKeys.autoDismissTime] ?: 5
        return Duration.ofMinutes(durationMinutes.toLong())
    }

    override suspend fun getAlarmVolume(): Int {
        return dataStorePreferences.data.first()[SettingsPreferencesKeys.alarmVolume] ?: 50
    }

    override suspend fun set24HourFormat(enabled: Boolean) {
        dataStorePreferences.edit { preferences ->
            preferences[SettingsPreferencesKeys.is24HourFormat] = enabled
        }
    }

    override suspend fun setDefaultRingtoneUri(uri: Uri) {
        dataStorePreferences.edit { preferences ->
            preferences[SettingsPreferencesKeys.defaultRingtoneUri] = uri.toString()
        }
    }

    override suspend fun setDefaultPreAlarmNotificationDuration(duration: Duration) {
        dataStorePreferences.edit { preferences ->
            preferences[SettingsPreferencesKeys.defaultPreAlarmNotificationMin] =
                duration.toMinutes().toInt()
        }
    }

    override suspend fun setAlarmVolume(volume: Int) {
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
            preferences[SettingsPreferencesKeys.snoozeDuration] = duration.toMinutes().toInt()
        }
    }

    override suspend fun setDefaultLabel(label: String) {
        dataStorePreferences.edit { preferences ->
            preferences[SettingsPreferencesKeys.defaultLabel] = label
        }
    }

    override suspend fun setAutoDismissTime(duration: Duration) {
        dataStorePreferences.edit { preferences ->
            preferences[SettingsPreferencesKeys.autoDismissTime] = duration.toMinutes().toInt()
        }
    }

    override suspend fun setGradualIncreaseVolume(duration: Duration) {
        dataStorePreferences.edit { preferences ->
            preferences[SettingsPreferencesKeys.gradualVolumeIncrease] = duration.seconds.toInt()
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
}

private object SettingsPreferencesKeys {
    val useDynamicColors = booleanPreferencesKey(name = "use_dynamic_colors")
    val is24HourFormat = booleanPreferencesKey("is_24_hour_format")
    val defaultRingtoneUri = stringPreferencesKey("default_ringtone_uri")
    val defaultPreAlarmNotificationMin = intPreferencesKey("default_pre_alarm_notification_min")
    val alarmVolume = intPreferencesKey("alarm_volume")
    val isVibrationEnabled = booleanPreferencesKey("vibration_enabled")
    val snoozeDuration = intPreferencesKey("snooze_duration") // store in minutes or seconds
    val defaultLabel = stringPreferencesKey("default_label")
    val autoDismissTime = intPreferencesKey("auto_dismiss_time") // in seconds
    val gradualVolumeIncrease = intPreferencesKey("gradual_volume_increase") // in seconds
    val themeStyle = stringPreferencesKey("theme_style")
    val isFirstTime = booleanPreferencesKey("is_first_time")
}