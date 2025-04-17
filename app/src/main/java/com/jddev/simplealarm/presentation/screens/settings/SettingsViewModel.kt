package com.jddev.simplealarm.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jddev.simplealarm.domain.model.alarm.Ringtone
import com.jddev.simplealarm.domain.model.settings.ThemeMode
import com.jddev.simplealarm.domain.repository.SettingsRepository
import com.jddev.simplealarm.domain.usecase.settings.InitializeAppSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val initializeAppSettingsUseCase: InitializeAppSettingsUseCase
) : ViewModel() {
    val themeMode = settingsRepository.themeSetting
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ThemeMode.SYSTEM)

    val is24hFormat = settingsRepository.is24HourFormat
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    val isUseDynamicColors = settingsRepository.isUseDynamicColors
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    val defaultRingtone = settingsRepository.defaultRingtone
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Ringtone.Silent)

    init {
        viewModelScope.launch {
            initializeAppSettingsUseCase(Unit)
        }
    }

    fun setThemeSetting(themeMode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.setThemeSetting(themeMode)
        }
    }

    fun on24hFormatChange(is24hFormat: Boolean) {
        viewModelScope.launch {
            settingsRepository.set24HourFormat(is24hFormat)
        }
    }

    fun setUseDynamicColors(enable: Boolean) {
        viewModelScope.launch {
            settingsRepository.setUseDynamicColors(enable)
        }
    }
}