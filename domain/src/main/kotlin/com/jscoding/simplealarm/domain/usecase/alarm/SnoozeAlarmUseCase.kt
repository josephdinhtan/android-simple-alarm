package com.jscoding.simplealarm.domain.usecase.alarm

import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.entity.alarm.NotificationType
import com.jscoding.simplealarm.domain.platform.AlarmRingingController
import com.jscoding.simplealarm.domain.platform.AlarmScheduler
import com.jscoding.simplealarm.domain.usecase.others.ShowNotificationUseCase
import java.util.Calendar
import javax.inject.Inject
import kotlin.time.Duration

class SnoozeAlarmUseCase @Inject constructor(
    private val alarmScheduler: AlarmScheduler,
    private val alarmRingingController: AlarmRingingController,
    private val showNotificationUseCase: ShowNotificationUseCase,
) {
    suspend operator fun invoke(alarm: Alarm) {
        // stop ringing
        alarmRingingController.snoozeRinging(alarm)

        // reschedule new snooze alarm
        val snoozeTime = calculateSnoozeTime(alarm.snoozeTime)
        val snoozeHour = snoozeTime.first
        val snoozeMinute = snoozeTime.second

        val snoozeAlarm = alarm.copy(
            hour = snoozeHour,
            minute = snoozeMinute,
            repeatDays = emptyList(),
            preAlarmNotificationDuration = Duration.ZERO,
            enabled = true,
        )

        alarmScheduler.schedule(snoozeAlarm)

        // show snooze notification
        showNotificationUseCase(
            snoozeAlarm,
            "Alarm snoozed",
            NotificationType.ALARM_SNOOZE
        )
    }

    private fun calculateSnoozeTime(snoozeDuration: Duration): Pair<Int, Int> {
        val currentTimeMillis = System.currentTimeMillis()
        val snoozeTimeMillis = currentTimeMillis + snoozeDuration.inWholeMilliseconds
        val calendar = Calendar.getInstance().apply {
            timeInMillis = snoozeTimeMillis
        }
        val snoozeHour = calendar.get(Calendar.HOUR_OF_DAY)
        val snoozeMinute = calendar.get(Calendar.MINUTE)
        return snoozeHour to snoozeMinute
    }

    private fun generateUniqueId(): Long {
        // Generate a unique ID, e.g., using a timestamp or random number
        return -System.nanoTime().toInt().toLong()
    }
}