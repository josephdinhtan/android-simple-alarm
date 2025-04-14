package com.jddev.simplealarm.data.repository

import android.net.Uri
import com.jddev.simplealarm.data.database.settings.SettingsDao
import com.jddev.simplealarm.data.database.settings.SettingsEntity
import com.jddev.simplealarm.domain.model.ThemeMode
import com.jddev.simplealarm.domain.repository.SettingsRepository
import javax.inject.Inject
import java.time.Duration

class SettingsRepositoryImpl @Inject constructor(
    private val settingsDao: SettingsDao,
) : SettingsRepository {

    private suspend fun initializeDefaultSettings() {
        val defaultSettings = SettingsEntity(
            id = 1,
            is24HourFormat = true,
            defaultRingtoneUri = "default_ringtone_uri",
            alarmVolume = 50,
            vibrationEnabled = true,
            snoozeDuration = Duration.ofMinutes(10).toMillis(),
            defaultLabel = "Alarm",
            autoDismissTime = Duration.ofMinutes(5).toMillis(),
            gradualVolumeEnabled = true,
            themeMode = ThemeMode.SYSTEM.ordinal,
            isFirstTime = false
        )
        settingsDao.insert(defaultSettings)
    }

    private suspend fun getSettingsEntity(): SettingsEntity {
        if (isFirstTime()) {
            initializeDefaultSettings()
        }
        return settingsDao.getSettings() ?: throw Exception("Settings not found")
    }

    override suspend fun is24HourFormat(): Boolean {
        return getSettingsEntity().is24HourFormat
    }

    override suspend fun set24HourFormat(enabled: Boolean) {
        val settings = getSettingsEntity().copy(is24HourFormat = enabled)
        settingsDao.update(settings)
    }

    override suspend fun getDefaultRingtoneUri(): Uri {
        return Uri.parse(getSettingsEntity().defaultRingtoneUri)
    }

    override suspend fun setDefaultRingtoneUri(uri: Uri) {
        val settings = getSettingsEntity().copy(defaultRingtoneUri = uri.toString())
        settingsDao.update(settings)
    }

    override suspend fun getAlarmVolume(): Int {
        return getSettingsEntity().alarmVolume
    }

    override suspend fun setAlarmVolume(volume: Int) {
        val settings = getSettingsEntity().copy(alarmVolume = volume)
        settingsDao.update(settings)
    }

    override suspend fun isVibrationEnabled(): Boolean {
        return getSettingsEntity().vibrationEnabled
    }

    override suspend fun setVibrationEnabled(enabled: Boolean) {
        val settings = getSettingsEntity().copy(vibrationEnabled = enabled)
        settingsDao.update(settings)
    }

    override suspend fun getSnoozeDuration(): Duration {
        return Duration.ofMillis(getSettingsEntity().snoozeDuration)
    }

    override suspend fun setSnoozeDuration(duration: Duration) {
        val settings = getSettingsEntity().copy(snoozeDuration = duration.toMillis())
        settingsDao.update(settings)
    }

    override suspend fun getDefaultLabel(): String {
        return getSettingsEntity().defaultLabel
    }

    override suspend fun setDefaultLabel(label: String) {
        val settings = getSettingsEntity().copy(defaultLabel = label)
        settingsDao.update(settings)
    }

    override suspend fun getAutoDismissTime(): Duration {
        return Duration.ofMillis(getSettingsEntity().autoDismissTime)
    }

    override suspend fun setAutoDismissTime(duration: Duration) {
        val settings = getSettingsEntity().copy(autoDismissTime = duration.toMillis())
        settingsDao.update(settings)
    }

    override suspend fun isGradualVolumeEnabled(): Boolean {
        return getSettingsEntity().gradualVolumeEnabled
    }

    override suspend fun setGradualVolumeEnabled(enabled: Boolean) {
        val settings = getSettingsEntity().copy(gradualVolumeEnabled = enabled)
        settingsDao.update(settings)
    }

    override suspend fun getThemeSetting(): ThemeMode {
        return ThemeMode.entries[getSettingsEntity().themeMode]
    }

    override suspend fun setThemeSetting(themeMode: ThemeMode) {
        val settings = getSettingsEntity().copy(themeMode = themeMode.ordinal)
        settingsDao.update(settings)
    }

    override suspend fun isFirstTime(): Boolean {
        return settingsDao.getSettings()?.isFirstTime ?: true
    }
}