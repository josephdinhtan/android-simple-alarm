package com.jscoding.simplealarm.domain.usecase.alarm

import com.jscoding.simplealarm.domain.platform.AlarmRingingController
import com.jscoding.simplealarm.domain.platform.AlarmScheduler
import com.jscoding.simplealarm.domain.repository.AlarmRepository
import com.jscoding.simplealarm.domain.usecase.SuspendUseCase
import javax.inject.Inject

class DismissAlarmUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler,
    private val alarmRingingController: AlarmRingingController
) : SuspendUseCase<Long, Unit> {
    override suspend fun invoke(params: Long) {
        alarmRepository.getAlarmById(params)?.let { alarm ->
            alarmRingingController.dismissRinging()
            alarmScheduler.cancel(params)
            // TODO: this for dismiss once alarm only, need handle for repeating alarm
            alarmRepository.updateAlarm(alarm.copy(enabled = false))
        }
    }
}