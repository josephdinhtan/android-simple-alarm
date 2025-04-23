package com.jscoding.simplealarm.domain.usecase.settings

import com.jscoding.simplealarm.domain.entity.alarm.Ringtone
import com.jscoding.simplealarm.domain.entity.settings.ThemeMode
import com.jscoding.simplealarm.domain.platform.SystemSettingsManager
import com.jscoding.simplealarm.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Singleton
class InitializeAppSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val systemSettingsManager: SystemSettingsManager,
) {
    suspend operator fun invoke() {
        val isFirstTime = settingsRepository.getIsFirstTimeStart()
        if (isFirstTime) {
            settingsRepository.setIsFirstTimeStart(false)

            // Initialize all default values here
            settingsRepository.set24HourFormat(systemSettingsManager.is24HourFormat())
            systemSettingsManager.getDefaultRingtone()?.let { systemDefaultTone ->
                settingsRepository.setDefaultRingtone(
                    Ringtone(
                        systemDefaultTone.title,
                        systemDefaultTone.uri
                    )
                )
            }
            settingsRepository.setDefaultPreAlarmNotificationDuration(5.minutes)
            settingsRepository.setVibrationEnabled(true)
            settingsRepository.setSnoozeDuration(5.minutes)
            settingsRepository.setDefaultLabel("Alarm")
            settingsRepository.setAutoDismissTime(5.minutes)
            settingsRepository.setVolumeFadeDuration(10.seconds)
            settingsRepository.setThemeSetting(ThemeMode.SYSTEM)
            settingsRepository.setUseDynamicColors(true)
        }
    }
}