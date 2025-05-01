package com.jscoding.simplealarm.data.repository

import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.jscoding.simplealarm.data.local.SettingsPreferencesKeys
import com.jscoding.simplealarm.domain.entity.alarm.Ringtone
import com.jscoding.simplealarm.domain.entity.settings.ThemeMode
import com.jscoding.simplealarm.domain.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
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
    override val volumeFadeDuration: Flow<Duration> = dataStorePreferences.data.map { preferences ->
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
    override val ringingTimeLimit: Flow<Duration> = dataStorePreferences.data.map { preferences ->
        val durationMinutes = preferences[SettingsPreferencesKeys.ringingTimeLimit] ?: 5
        durationMinutes.minutes
    }

    override suspend fun getIs24HourFormat(): Boolean = withContext(Dispatchers.IO) {
        dataStorePreferences.data.first()[SettingsPreferencesKeys.is24HourFormat] ?: true
    }

    override suspend fun getDefaultPreAlarmNotificationDuration(): Duration =
        withContext(Dispatchers.IO) {
            val durationMinutes =
                dataStorePreferences.data.first()[SettingsPreferencesKeys.defaultPreAlarmNotificationMin]
                    ?: 5
            durationMinutes.minutes
        }

    override suspend fun getSnoozeDuration(): Duration = withContext(Dispatchers.IO) {
        val durationMinutes =
            dataStorePreferences.data.first()[SettingsPreferencesKeys.snoozeDuration] ?: 5
        durationMinutes.minutes
    }

    override suspend fun getAutoDismissTime(): Duration = withContext(Dispatchers.IO) {
        val durationMinutes =
            dataStorePreferences.data.first()[SettingsPreferencesKeys.autoDismissTime] ?: 5
        durationMinutes.minutes
    }

    override suspend fun getVolumeFadeDuration(): Duration = withContext(Dispatchers.IO) {
        val durationSeconds =
            dataStorePreferences.data.first()[SettingsPreferencesKeys.volumeFadeDuration] ?: 0
        durationSeconds.seconds
    }

    override suspend fun getIsFirstTimeStart(): Boolean = withContext(Dispatchers.IO) {
        dataStorePreferences.data.first()[SettingsPreferencesKeys.isFirstTime] ?: true
    }

    override suspend fun getDefaultRingtone(): Ringtone = withContext(Dispatchers.IO) {
        val defaultRingtoneUriStr =
            dataStorePreferences.data.first()[SettingsPreferencesKeys.defaultRingtoneUri]
        val defaultRingtoneTitle =
            dataStorePreferences.data.first()[SettingsPreferencesKeys.defaultRingtoneTitle]
        if (defaultRingtoneUriStr != null && defaultRingtoneTitle != null) {
            Ringtone(defaultRingtoneTitle, Uri.parse(defaultRingtoneUriStr))
        } else {
            Ringtone.Silent
        }
    }

    override suspend fun getRingingTimeLimit(): Duration {
        val durationMinutes =
            dataStorePreferences.data.first()[SettingsPreferencesKeys.ringingTimeLimit] ?: 5
        return durationMinutes.minutes
    }

    override suspend fun set24HourFormat(enabled: Boolean) = withContext(Dispatchers.IO) {
        dataStorePreferences.edit { preferences ->
            preferences[SettingsPreferencesKeys.is24HourFormat] = enabled
        }
        Unit
    }

    override suspend fun setDefaultRingtone(ringtone: Ringtone) = withContext(Dispatchers.IO) {
        dataStorePreferences.edit { preferences ->
            preferences[SettingsPreferencesKeys.defaultRingtoneUri] = ringtone.uri.toString()
            preferences[SettingsPreferencesKeys.defaultRingtoneTitle] = ringtone.title
        }
        Unit
    }

    override suspend fun setDefaultPreAlarmNotificationDuration(duration: Duration) =
        withContext(Dispatchers.IO) {
            dataStorePreferences.edit { preferences ->
                preferences[SettingsPreferencesKeys.defaultPreAlarmNotificationMin] =
                    duration.inWholeMinutes.toInt()
            }
            Unit
        }

    override suspend fun setVibrationEnabled(enabled: Boolean) = withContext(Dispatchers.IO) {
        dataStorePreferences.edit { preferences ->
            preferences[SettingsPreferencesKeys.isVibrationEnabled] = enabled
        }
        Unit
    }

    override suspend fun setSnoozeDuration(duration: Duration) = withContext(Dispatchers.IO) {
        dataStorePreferences.edit { preferences ->
            preferences[SettingsPreferencesKeys.snoozeDuration] = duration.inWholeMinutes.toInt()
        }
        Unit
    }

    override suspend fun setDefaultLabel(label: String) = withContext(Dispatchers.IO) {
        dataStorePreferences.edit { preferences ->
            preferences[SettingsPreferencesKeys.defaultLabel] = label
        }
        Unit
    }

    override suspend fun setAutoDismissTime(duration: Duration) = withContext(Dispatchers.IO) {
        dataStorePreferences.edit { preferences ->
            preferences[SettingsPreferencesKeys.autoDismissTime] = duration.inWholeMinutes.toInt()
        }
        Unit
    }

    override suspend fun setVolumeFadeDuration(duration: Duration) = withContext(Dispatchers.IO) {
        dataStorePreferences.edit { preferences ->
            preferences[SettingsPreferencesKeys.volumeFadeDuration] =
                duration.inWholeSeconds.toInt()
        }
        Unit
    }

    override suspend fun setThemeSetting(themeMode: ThemeMode) = withContext(Dispatchers.IO) {
        dataStorePreferences.edit { preferences ->
            preferences[SettingsPreferencesKeys.themeStyle] = themeMode.name
        }
        Unit
    }

    override suspend fun setUseDynamicColors(enabled: Boolean) = withContext(Dispatchers.IO) {
        dataStorePreferences.edit { preferences ->
            preferences[SettingsPreferencesKeys.useDynamicColors] = enabled
        }
        Unit
    }

    override suspend fun setIsFirstTimeStart(value: Boolean) = withContext(Dispatchers.IO) {
        dataStorePreferences.edit { preferences ->
            preferences[SettingsPreferencesKeys.isFirstTime] = value
        }
        Unit
    }

    override suspend fun setRingingTimeLimit(duration: Duration) = withContext(Dispatchers.IO) {
        dataStorePreferences.edit { preferences ->
            preferences[SettingsPreferencesKeys.ringingTimeLimit] = duration.inWholeMinutes.toInt()
        }
        Unit
    }
}