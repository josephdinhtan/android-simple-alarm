package com.jddev.simplealarm.domain.usecase.settings

import com.jddev.simplealarm.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateTimeFormatUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

}