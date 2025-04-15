package com.jddev.simplealarm.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jddev.simplealarm.domain.model.settings.ThemeMode
import com.jddev.simplealarm.domain.repository.SettingsRepository
import com.jddev.simplealarm.domain.utils.Result.Loading.getOrDefault
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

//    private val _themeMode = MutableStateFlow<ThemeMode>(ThemeMode.SYSTEM)
//    val themeMode = _themeMode.asStateFlow()

    val themeMode = settingsRepository.themeSetting
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ThemeMode.SYSTEM)

    val is24hFormat = settingsRepository.is24HourFormat
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    val isUseDynamicColors = settingsRepository.isUseDynamicColors
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    init {
        viewModelScope.launch {
//            _themeMode.value = settingsRepository.getThemeSetting().getOrDefault(ThemeMode.SYSTEM)
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