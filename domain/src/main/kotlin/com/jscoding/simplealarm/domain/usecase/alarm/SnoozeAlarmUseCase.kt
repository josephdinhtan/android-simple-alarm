package com.jscoding.simplealarm.domain.usecase.alarm

import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.entity.alarm.NotificationType
import com.jscoding.simplealarm.domain.platform.AlarmRingingController
import com.jscoding.simplealarm.domain.platform.AlarmScheduler
import com.jscoding.simplealarm.domain.usecase.others.ShowNotificationUseCase
import com.jscoding.simplealarm.domain.utils.getNextTriggerTime
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject
import kotlin.time.Duration

/**
 * Snooze an Alarm
 *
 * Triggered when the user click snooze button
 */
class SnoozeAlarmUseCase @Inject constructor(
    private val alarmScheduler: AlarmScheduler,
    private val alarmRingingController: AlarmRingingController,
    private val showNotificationUseCase: ShowNotificationUseCase,
) {
    suspend operator fun invoke(alarm: Alarm) {
        // stop ringing
        alarmRingingController.snoozeAlarm(alarm)

        // reschedule new snooze alarm
        val snoozeTime = calculateSnoozeTimeFromNow(alarm.snoozeTime)
        val snoozeHour = snoozeTime.first
        val snoozeMinute = snoozeTime.second

        val snoozeAlarm = alarm.copy(
            hour = snoozeHour,
            minute = snoozeMinute,
            repeatDays = emptyList(),
            preAlarmNotificationDuration = Duration.ZERO,
            enabled = true,
        )
        Timber.d("Snooze alarm: ${snoozeAlarm.label}, id: ${snoozeAlarm.id}")

        alarmScheduler.schedule(snoozeAlarm, snoozeAlarm.getNextTriggerTime())

        // show snooze notification
        showNotificationUseCase(
            snoozeAlarm,
            "Alarm snoozed",
            NotificationType.ALARM_SNOOZE
        )
    }

    private fun calculateSnoozeTimeFromNow(snoozeDuration: Duration): Pair<Int, Int> {
        val currentTimeMillis = System.currentTimeMillis()
        val snoozeTimeMillis = currentTimeMillis + snoozeDuration.inWholeMilliseconds
        val calendar = Calendar.getInstance().apply {
            timeInMillis = snoozeTimeMillis
        }
        val snoozeHour = calendar.get(Calendar.HOUR_OF_DAY)
        val snoozeMinute = calendar.get(Calendar.MINUTE)
        return snoozeHour to snoozeMinute
    }
}