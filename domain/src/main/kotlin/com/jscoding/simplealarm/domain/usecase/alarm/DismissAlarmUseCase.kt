package com.jscoding.simplealarm.domain.usecase.alarm

import com.jscoding.simplealarm.domain.platform.AlarmRingingController
import com.jscoding.simplealarm.domain.platform.AlarmScheduler
import com.jscoding.simplealarm.domain.repository.AlarmRepository
import javax.inject.Inject

class DismissAlarmUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler,
    private val alarmRingingController: AlarmRingingController,
) {
    suspend operator fun invoke(alarmId: Long) {
        alarmRepository.getAlarmById(alarmId)?.let { alarm ->
            alarmRingingController.dismissRinging()
            alarmScheduler.cancel(alarmId)
            // TODO: this for dismiss once alarm only, need handle for repeating alarm
            alarmRepository.updateAlarm(alarm.copy(enabled = false))
        }
    }
}