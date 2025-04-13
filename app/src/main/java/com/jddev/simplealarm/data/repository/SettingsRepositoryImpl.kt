package com.jddev.simplealarm.data.repository

import com.jddev.simplealarm.domain.repository.SettingsRepository
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor() : SettingsRepository {
    override fun getDarkThemeMode(): Int {
        return 0
    }
}