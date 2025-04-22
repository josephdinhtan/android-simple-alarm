package com.jscoding.simplealarm.domain.usecase.settings

import com.jscoding.simplealarm.domain.platform.SystemSettingsManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetMaxAlarmVolumeUseCase @Inject constructor(
    private val systemSettingsManager: SystemSettingsManager,
) {
    operator fun invoke(): Int {
        return systemSettingsManager.getMaxAlarmVolume()
    }
}