package com.jddev.simplealarm.domain.usecase.settings

import com.jddev.simplealarm.domain.model.alarm.Ringtone
import com.jddev.simplealarm.domain.model.settings.ThemeMode
import com.jddev.simplealarm.domain.repository.SettingsRepository
import com.jddev.simplealarm.domain.platform.SystemSettingsManager
import com.jddev.simplealarm.domain.usecase.SuspendUseCase
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Singleton
class InitializeAppSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val systemSettingsManager: SystemSettingsManager,
) : SuspendUseCase<Unit, Unit> {
    override suspend fun invoke(params: Unit) {

        val isFirstTime = settingsRepository.getIsFirstTimeStart()
        if (true) {
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