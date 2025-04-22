package com.jscoding.simplealarm.domain.usecase.settings

import com.jscoding.simplealarm.domain.platform.SystemSettingsManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetAlarmVolumeUseCase @Inject constructor(
    private val systemSettingsManager: SystemSettingsManager
) {
    operator fun invoke(params: Int) {
        val volume = params.coerceIn(0, systemSettingsManager.getMaxAlarmVolume())
        systemSettingsManager.setAlarmVolume(volume)
    }
}