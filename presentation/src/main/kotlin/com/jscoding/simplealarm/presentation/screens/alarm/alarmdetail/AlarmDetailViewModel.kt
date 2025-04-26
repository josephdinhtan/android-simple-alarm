package com.jscoding.simplealarm.presentation.screens.alarm.alarmdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.entity.alarm.Ringtone
import com.jscoding.simplealarm.domain.platform.SystemSettingsManager
import com.jscoding.simplealarm.domain.repository.SettingsRepository
import com.jscoding.simplealarm.domain.usecase.alarm.AddAlarmUseCase
import com.jscoding.simplealarm.domain.usecase.alarm.DeleteAlarmUseCase
import com.jscoding.simplealarm.domain.usecase.alarm.GetAlarmByIdUseCase
import com.jscoding.simplealarm.domain.usecase.alarm.UpdateAlarmUseCase
import com.jscoding.simplealarm.domain.usecase.others.StartPlayToneUseCase
import com.jscoding.simplealarm.domain.usecase.others.StopPlayToneUseCase
import com.jscoding.simplealarm.presentation.utils.default
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
class AlarmDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getAlarmByIdUseCase: GetAlarmByIdUseCase,
    private val settingsRepository: SettingsRepository,
    private val addAlarmUseCase: AddAlarmUseCase,
    private val deleteAlarmUseCase: DeleteAlarmUseCase,
    private val updateAlarmUseCase: UpdateAlarmUseCase,
    private val systemSettingsManager: SystemSettingsManager,
    private val startPlayToneUseCase: StartPlayToneUseCase,
    private val stopPlayToneUseCase: StopPlayToneUseCase,
) : ViewModel() {

    sealed class UiState {
        data object Loading : UiState()
        data class Success(val alarm: Alarm) : UiState()
        data class Error(val message: String) : UiState()
    }

    // Alarm Detail screen
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    // Internal alarm model
    private var currentAlarm: Alarm? = null

    // Ringtone screen
    private val _availableRingtones = MutableStateFlow<List<Ringtone>>(emptyList())
    val availableRingtones = _availableRingtones.asStateFlow()

    private val _selectedRingtone = MutableStateFlow(Ringtone.Silent)
    val selectedRingtone = _selectedRingtone.asStateFlow()

    private val _isTonePlaying = MutableStateFlow<Boolean>(false)
    val isTonePlaying = _isTonePlaying.asStateFlow()

    val is24hFormat = settingsRepository.is24HourFormat.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        true
    )

    init {
        val alarmId: Long? = savedStateHandle["alarm_id"]
        if (alarmId == null || alarmId == -1L) {
            setupNewAlarm()
        } else {
            editAlarm(alarmId)
        }
    }

    private fun setupNewAlarm() {
        val calendar = Calendar.getInstance()
        val hour24hrs: Int = calendar.get(Calendar.HOUR_OF_DAY)
        val minutes: Int = calendar.get(Calendar.MINUTE)
        viewModelScope.launch(Dispatchers.IO) {
            val defaultRingtone = settingsRepository.getDefaultRingtone()
            val currentAlarm = Alarm(
                id = -1L,
                hour = hour24hrs,
                minute = minutes,
                ringtone = defaultRingtone,
                label = "",
                repeatDays = emptyList(),
                vibration = true,
                enabled = true,
                snoozeTime = 5.minutes,
                preAlarmNotificationDuration = 5.minutes,
                createdAt = System.currentTimeMillis(),
            )
            _uiState.value = UiState.Success(currentAlarm)
        }
    }

    private fun editAlarm(alarmId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            currentAlarm = getAlarmByIdUseCase(alarmId) ?: Alarm.default()
            currentAlarm?.let {
                _uiState.value = UiState.Success(it)
            }
        }
    }

    fun addNewAlarm() {
        currentAlarm?.let {
            viewModelScope.launch(Dispatchers.IO) {
                addAlarmUseCase(it)
            }
        }
    }

    fun deleteAlarm() {
        currentAlarm?.let {
            viewModelScope.launch(Dispatchers.IO) {
                deleteAlarmUseCase(it.id)
            }
        }
    }

    fun saveAlarm() {
        viewModelScope.launch(Dispatchers.IO) {
            currentAlarm?.let {
                updateAlarmUseCase(it.copy(enabled = true))
            }
        }
    }

    fun onAlarmValueChange(alarm: Alarm) {
        currentAlarm = alarm
        currentAlarm?.let {
            _uiState.value = UiState.Success(it)
        }
    }

    // Ringtone Screen
    fun setupRingtoneScreen() {
        viewModelScope.launch(Dispatchers.IO) {
            val systemRingtones = systemSettingsManager.getRingtones()
            val defaultRingtone = systemSettingsManager.getDefaultRingtone()
            val allRingtones = defaultRingtone?.let { listOf(it) + systemRingtones } ?: systemRingtones
            _availableRingtones.value = allRingtones
            currentAlarm?.let {
                _selectedRingtone.value = it.ringtone
            }
        }
    }

    fun onRingtoneSave() {
        currentAlarm?.let {
            onAlarmValueChange(it.copy(ringtone = selectedRingtone.value))
        }
    }

    fun onRingtoneSelectedAndPlayTone(ringtone: Ringtone) {
        _selectedRingtone.value = ringtone
        viewModelScope.launch(Dispatchers.IO) {
            stopPlayToneUseCase()
            delay(100)
            startPlayToneUseCase(ringtone)
            _isTonePlaying.value = true
        }
    }

    fun stopPlayTone() {
        stopPlayToneUseCase()
        _isTonePlaying.value = false
    }
}