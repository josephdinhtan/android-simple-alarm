package com.jscoding.simplealarm.domain.usecase.alarm

import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.platform.AlarmRingingController
import com.jscoding.simplealarm.domain.platform.AlarmScheduler
import com.jscoding.simplealarm.domain.repository.AlarmRepository
import javax.inject.Inject

class DismissAlarmUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler,
    private val alarmRingingController: AlarmRingingController,
) {
    suspend operator fun invoke(alarm: Alarm) {
        alarmRingingController.dismissRinging(alarm)
        alarmScheduler.cancel(alarm)
        if(alarm.repeatDays.isEmpty()) {
            alarmRepository.updateAlarm(alarm.copy(enabled = false))
        }
    }
}