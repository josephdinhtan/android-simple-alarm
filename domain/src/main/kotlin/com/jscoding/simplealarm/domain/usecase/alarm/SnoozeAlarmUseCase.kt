package com.jscoding.simplealarm.domain.usecase.alarm

import com.jscoding.simplealarm.domain.platform.AlarmRingingController
import com.jscoding.simplealarm.domain.platform.AlarmScheduler
import com.jscoding.simplealarm.domain.repository.AlarmRepository
import com.jscoding.simplealarm.domain.usecase.SuspendUseCase
import java.util.Calendar
import javax.inject.Inject

class SnoozeAlarmUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler,
    private val alarmRingingController: AlarmRingingController,
) : SuspendUseCase<Long, Unit> {
    override suspend fun invoke(params: Long) {
        alarmRepository.getAlarmById(params)?.let { alarm ->
            alarmRingingController.snoozeRinging()

            // TODO: this for dismiss once alarm only, need handle for repeating alarm
            val snoozeTime = System.currentTimeMillis() + alarm.snoozeTime.inWholeMilliseconds
            val calendar = Calendar.getInstance().apply {
                timeInMillis = snoozeTime
            }
            alarmScheduler.schedule(
                alarm.id,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE)
            )
        }
    }
}