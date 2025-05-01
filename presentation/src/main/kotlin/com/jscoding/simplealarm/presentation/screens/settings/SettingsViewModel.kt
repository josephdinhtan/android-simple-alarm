package com.jscoding.simplealarm.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jscoding.simplealarm.domain.entity.alarm.Ringtone
import com.jscoding.simplealarm.domain.entity.settings.ThemeMode
import com.jscoding.simplealarm.domain.repository.SettingsRepository
import com.jscoding.simplealarm.domain.usecase.settings.GetAlarmVolumeUseCase
import com.jscoding.simplealarm.domain.usecase.settings.GetMaxAlarmVolumeUseCase
import com.jscoding.simplealarm.domain.usecase.settings.InitializeAppSettingsUseCase
import com.jscoding.simplealarm.domain.usecase.settings.SetAlarmVolumeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val initializeAppSettingsUseCase: InitializeAppSettingsUseCase,
    private val getMaxAlarmVolumeUseCase: GetMaxAlarmVolumeUseCase,
    private val getAlarmVolumeUseCase: GetAlarmVolumeUseCase,
    private val setAlarmVolumeUseCase: SetAlarmVolumeUseCase,
) : ViewModel() {
    val themeMode = settingsRepository.themeSetting
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ThemeMode.SYSTEM)

    val is24hFormat = settingsRepository.is24HourFormat
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    val isUseDynamicColors = settingsRepository.isUseDynamicColors
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    val defaultRingtone = settingsRepository.defaultRingtone
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Ringtone.Silent)

    val ringingTimeLimit = settingsRepository.ringingTimeLimit
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 5.minutes)

    private val _maxAlarmVolume = MutableStateFlow<Int>(10)
    val maxAlarmVolume = _maxAlarmVolume.asStateFlow()

    private val _currentAlarmVolume = MutableStateFlow<Int>(5)
    val currentAlarmVolume = _currentAlarmVolume.asStateFlow()

    init {
        viewModelScope.launch {
            initializeAppSettingsUseCase()
        }
        viewModelScope.launch {
            _maxAlarmVolume.value = getMaxAlarmVolumeUseCase()
        }
        viewModelScope.launch {
            _currentAlarmVolume.value = getAlarmVolumeUseCase()
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

    fun setAlarmVolume(volume: Int) {
        if (volume <= 0 || volume > _maxAlarmVolume.value) return
        _currentAlarmVolume.value = volume
        viewModelScope.launch {
            setAlarmVolumeUseCase(volume)
        }
        // TODO: consider play sound
    }

    fun setRingingTimeLimit(duration: Duration) {
        viewModelScope.launch {
            settingsRepository.setRingingTimeLimit(duration)
        }
    }
}