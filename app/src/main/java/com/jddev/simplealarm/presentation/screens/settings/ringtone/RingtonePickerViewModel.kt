package com.jddev.simplealarm.presentation.screens.settings.ringtone

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jddev.simplealarm.domain.model.alarm.Ringtone
import com.jddev.simplealarm.domain.repository.SettingsRepository
import com.jddev.simplealarm.domain.system.SystemSettingsManager
import com.jddev.simplealarm.domain.usecase.alarm.GetAlarmByIdUseCase
import com.jddev.simplealarm.domain.usecase.alarm.UpdateAlarmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RingtonePickerViewModel @Inject constructor(
    private val systemSettingsManager: SystemSettingsManager,
    private val settingsRepository: SettingsRepository,
    private val getAlarmByIdUseCase: GetAlarmByIdUseCase,
    private val updateAlarmUseCase: UpdateAlarmUseCase,
) : ViewModel() {

    private val _availableRingtones = MutableStateFlow<List<Ringtone>>(emptyList())
    val availableRingtones = _availableRingtones.asStateFlow()

    private val _selectedRingtone = MutableStateFlow(Ringtone.Silent)
    val selectedRingtone = _selectedRingtone.asStateFlow()

    init {
        viewModelScope.launch {
            val systemRingtones = systemSettingsManager.getRingtones()
            val defaultRingtone = systemSettingsManager.getDefaultRingtone()
            defaultRingtone?.let { ringtone ->
                _availableRingtones.value = listOf(ringtone) + systemRingtones
            } ?: run {
                _availableRingtones.value = systemRingtones
            }
        }

        viewModelScope.launch {
            selectedRingtone.collect {
                Timber.e("selectedRingtone = $it")
            }
        }

        viewModelScope.launch {
            availableRingtones.collect {
                for (ringtone in it) {
                    Timber.e("availableRingtones = $ringtone")
                }
            }
        }
    }

    fun onRingtoneSelected(ringtone: Ringtone) {
        _selectedRingtone.value = ringtone
        // TODO: play tone here
    }

    fun getDefaultRingtone() {
        viewModelScope.launch {
            _selectedRingtone.value = settingsRepository.getDefaultRingtone()
        }
    }

    fun setDefaultRingtone(ringtone: Ringtone) {
        viewModelScope.launch {
            settingsRepository.setDefaultRingtone(ringtone)
        }
    }

    fun getAlarmSelectedRingtone(alarmId: Long) {
        viewModelScope.launch {
            val alarm = getAlarmByIdUseCase(alarmId)
            alarm?.let {
                _selectedRingtone.value = it.tone
            }
        }
    }

    fun setAlarmRingtone(ringtone: Ringtone, alarmId: Long) {
        viewModelScope.launch {
            val alarm = getAlarmByIdUseCase(alarmId)
            alarm?.let {
                updateAlarmUseCase(it.copy(tone = ringtone))
            }
        }
    }
}