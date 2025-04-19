package com.jscoding.simplealarm.domain.usecase.settings

import com.jscoding.simplealarm.domain.platform.SystemSettingsManager
import com.jscoding.simplealarm.domain.usecase.SuspendUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetAlarmVolumeUseCase @Inject constructor(
    private val systemSettingsManager: SystemSettingsManager
) : SuspendUseCase<Int, Unit> {

    override suspend fun invoke(params: Int) {
        val volume = params.coerceIn(0, systemSettingsManager.getMaxAlarmVolume())
        systemSettingsManager.setAlarmVolume(volume)
    }
}