package com.jddev.simplealarm.domain.usecase.settings

import com.jddev.simplealarm.domain.repository.SettingsRepository
import com.jddev.simplealarm.domain.system.SystemSettingsManager
import com.jddev.simplealarm.domain.usecase.SuspendUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetDefaultAlarmVolumeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val systemSettingsManager: SystemSettingsManager
) : SuspendUseCase<Unit, Int> {

    override suspend fun invoke(params: Unit): Int {
        val volumeInSystem = systemSettingsManager.getAlarmVolume()
        settingsRepository.setAlarmVolume(volumeInSystem)
        return volumeInSystem
    }
}