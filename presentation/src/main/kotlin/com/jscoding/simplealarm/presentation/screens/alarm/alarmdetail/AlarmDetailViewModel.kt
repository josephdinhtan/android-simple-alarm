package com.jscoding.simplealarm.presentation.screens.alarm.alarmdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.entity.alarm.Ringtone
import com.jscoding.simplealarm.domain.entity.exceptions.AlarmAlreadyExistsException
import com.jscoding.simplealarm.domain.entity.exceptions.NotificationNotAllowException
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
class AlarmDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getAlarmByIdUseCase: GetAlarmByIdUseCase,
    private val settingsRepository: SettingsRepository,
    private val addAlarmUseCase: AddAlarmUseCase,
    private val deleteAlarmUseCase: DeleteAlarmUseCase,
    private val updateAlarmUseCase: UpdateAlarmUseCase,
    private val systemSettingsManager: SystemSettingsManager,
    private val startPlayToneUseCase: StartPlayToneUseCase,
    private val stopPlayToneUseCase: StopPlayToneUseCase,
) : ViewModel() {

    sealed interface AlarmDetailEvent {
        data object SaveSuccess : AlarmDetailEvent
        data object NotificationNotAllow : AlarmDetailEvent
        data class Error(val message: String, val needExit: Boolean = false) : AlarmDetailEvent
    }

    sealed interface UiState {
        data object Loading : UiState
        data class Success(val alarm: Alarm, val isNewAlarm: Boolean) : UiState
        data class Error(val message: String) : UiState
    }

    // Alarm Detail screen
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<AlarmDetailEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    // Internal alarm model
    private var upToDateAlarm: Alarm? = null
    private var originalAlarm: Alarm? = null

    // Ringtone screen
    private val _availableRingtones = MutableStateFlow<List<Ringtone>>(emptyList())
    val availableRingtones = _availableRingtones.asStateFlow()

    private val _selectedRingtone = MutableStateFlow(Ringtone.Silent)
    val selectedRingtone = _selectedRingtone.asStateFlow()

    private val _isTonePlaying = MutableStateFlow(false)
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
            setupEditAlarm(alarmId)
        }
    }

    private fun setupNewAlarm() {
        val calendar = Calendar.getInstance()
        val hour24hrs: Int = calendar.get(Calendar.HOUR_OF_DAY)
        val minutes: Int = calendar.get(Calendar.MINUTE)
        viewModelScope.launchIo {
            val defaultRingtone = settingsRepository.getDefaultRingtone()
            val newAlarm = Alarm(
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
            upToDateAlarm = newAlarm
            _uiState.value = UiState.Success(newAlarm, true)
        }
    }

    private fun setupEditAlarm(alarmId: Long) {
        viewModelScope.launchIo {
            upToDateAlarm = getAlarmByIdUseCase(alarmId) ?: Alarm.default()
            originalAlarm = upToDateAlarm?.copy()
            upToDateAlarm?.let {
                _uiState.value = UiState.Success(it, false)
            }
        }
    }

    fun deleteAlarm() {
        upToDateAlarm?.let {
            viewModelScope.launchIo {
                deleteAlarmUseCase(it)
            }
        }
    }

    fun saveAlarm() {
        val state = uiState.value
        val alarm = upToDateAlarm

        if (state !is UiState.Success || alarm == null) return

        viewModelScope.launchIo {
            if (state.isNewAlarm) {
                val result = addAlarmUseCase(alarm)
                result.onSuccess {
                    _eventFlow.emit(AlarmDetailEvent.SaveSuccess)
                }.onFailure { exception ->
                    when (exception) {
                        is AlarmAlreadyExistsException -> {
                            _eventFlow.emit(AlarmDetailEvent.Error(exception.message, true))
                        }

                        is NotificationNotAllowException -> {
                            _eventFlow.emit(AlarmDetailEvent.NotificationNotAllow)
                        }

                        else -> {
                            _eventFlow.emit(
                                AlarmDetailEvent.Error(
                                    exception.message ?: "Unknown error", true
                                )
                            )
                        }
                    }
                }
            } else {
                originalAlarm?.let {
                    updateAlarmUseCase(it, alarm.copy(enabled = true))
                    _eventFlow.emit(AlarmDetailEvent.SaveSuccess)
                } ?: run {
                    _eventFlow.emit(AlarmDetailEvent.Error("No original alarm found", true))
                }
            }
        }
    }

    fun onAlarmValueChange(alarm: Alarm) {
        upToDateAlarm = alarm
        _uiState.update { state ->
            if (state is UiState.Success) {
                state.copy(alarm = alarm)
            } else {
                state
            }
        }
    }

    // Ringtone Screen
    fun setupRingtoneScreen() {
        viewModelScope.launchIo {
            val systemRingtones = systemSettingsManager.getRingtones()
            val defaultRingtone = systemSettingsManager.getDefaultRingtone()
            val allRingtones =
                defaultRingtone?.let { listOf(it) + systemRingtones } ?: systemRingtones
            _availableRingtones.value = allRingtones
            upToDateAlarm?.let {
                _selectedRingtone.value = it.ringtone
            }
        }
    }

    fun onRingtoneSave() {
        upToDateAlarm?.let {
            onAlarmValueChange(it.copy(ringtone = selectedRingtone.value))
        }
    }

    fun onRingtoneSelectedAndPlayTone(ringtone: Ringtone) {
        _selectedRingtone.value = ringtone
        viewModelScope.launchIo {
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

    private fun CoroutineScope.launchIo(block: suspend () -> Unit) {
        launch(Dispatchers.IO) {
            block()
        }
    }
}