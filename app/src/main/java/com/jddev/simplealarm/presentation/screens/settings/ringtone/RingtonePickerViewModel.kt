package com.jddev.simplealarm.presentation.screens.settings.ringtone

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jddev.simplealarm.domain.model.alarm.Ringtone
import com.jddev.simplealarm.domain.repository.SettingsRepository
import com.jddev.simplealarm.domain.system.SystemSettingsManager
import com.jddev.simplealarm.domain.usecase.alarm.GetAlarmByIdUseCase
import com.jddev.simplealarm.domain.usecase.alarm.UpdateAlarmUseCase
import com.jddev.simplealarm.domain.usecase.others.StartPlayToneUseCase
import com.jddev.simplealarm.domain.usecase.others.StopPlayToneUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
    private val startPlayToneUseCase: StartPlayToneUseCase,
    private val stopPlayToneUseCase: StopPlayToneUseCase,
) : ViewModel() {

    private val _availableRingtones = MutableStateFlow<List<Ringtone>>(emptyList())
    val availableRingtones = _availableRingtones.asStateFlow()

    private val _selectedRingtone = MutableStateFlow(Ringtone.Silent)
    val selectedRingtone = _selectedRingtone.asStateFlow()

    private val _isTonePlaying = MutableStateFlow<Boolean>(false)
    val isTonePlaying = _isTonePlaying.asStateFlow()

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

    fun onRingtoneSelectedAndPlayTone(ringtone: Ringtone) {
        _selectedRingtone.value = ringtone
        viewModelScope.launch(Dispatchers.Main) {
            stopPlayToneUseCase(Unit)
            _isTonePlaying.value = false
            delay(100)
            startPlayToneUseCase(ringtone)
            _isTonePlaying.value = true
        }
    }

    fun stopPlayTone() {
        viewModelScope.launch {
            stopPlayToneUseCase(Unit)
            _isTonePlaying.value = false
        }
    }

    override fun onCleared() {
        viewModelScope.launch {
            stopPlayToneUseCase(Unit)
            _isTonePlaying.value = false
        }
        super.onCleared()
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