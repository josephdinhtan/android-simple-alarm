package com.jscoding.simplealarm.domain.usecase.alarm

import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.repository.AlarmRepository
import javax.inject.Inject

/**
 * Delete an Alarm
 */
class DeleteAlarmUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val cancelScheduleAlarmUseCase: CancelScheduleAlarmUseCase
) {
    suspend operator fun invoke(alarm: Alarm): Result<Unit> {
        // Cancel current schedule related to this Alarm
        cancelScheduleAlarmUseCase(alarm)
        // Delete Alarm
        alarmRepository.deleteAlarm(alarm)

        // TODO: hard code success
        return Result.success(Unit)
    }
}