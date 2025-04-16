package com.jddev.simplealarm.domain.usecase.settings

import com.jddev.simplealarm.domain.repository.SettingsRepository
import com.jddev.simplealarm.domain.system.SystemSettingsManager
import com.jddev.simplealarm.domain.usecase.SuspendUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetDefaultAlarmVolumeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val systemSettingsManager: SystemSettingsManager
) : SuspendUseCase<Int, Unit> {

    override suspend fun invoke(params: Int) {
        if(params in 0..systemSettingsManager.getMaxAlarmVolume()) {
//            settingsRepository.setAlarmVolume(params)
            systemSettingsManager.setAlarmVolume(params)
        }
    }
}