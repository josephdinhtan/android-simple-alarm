package com.jscoding.simplealarm.domain.usecase.settings

import com.jscoding.simplealarm.domain.platform.SystemSettingsManager
import com.jscoding.simplealarm.domain.usecase.SuspendUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetMaxAlarmVolumeUseCase @Inject constructor(
    private val systemSettingsManager: SystemSettingsManager,
) : SuspendUseCase<Unit, Int> {

    override suspend fun invoke(params: Unit): Int {
        return systemSettingsManager.getMaxAlarmVolume()
    }
}