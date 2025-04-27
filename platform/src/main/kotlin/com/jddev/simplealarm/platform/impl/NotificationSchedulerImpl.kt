package com.jddev.simplealarm.platform.impl

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.jddev.simplealarm.platform.helper.AlarmIntentProvider
import com.jddev.simplealarm.platform.helper.AlarmManagerHelper
import com.jddev.simplealarm.platform.utils.calculateNextTriggerTime
import com.jddev.simplealarm.platform.utils.calculateTriggerTime
import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.entity.alarm.DayOfWeek
import com.jscoding.simplealarm.domain.platform.AlarmNotificationScheduler
import javax.inject.Inject

class NotificationSchedulerImpl @Inject constructor(
    private val alarmManagerHelper: AlarmManagerHelper,
    private val intentProvider: AlarmIntentProvider,
    private val context: Context,
) : AlarmNotificationScheduler {

    override fun schedule(
        alarm: Alarm,
        is24HourFormat: Boolean,
    ) {
        if (alarm.repeatDays.isEmpty()) {
            val triggerTime = calculateTriggerTime(alarm.hour, alarm.minute)
            val pendingIntent =
                intentProvider.provideNotificationIntent(
                    alarmId = alarm.id,
                    label = alarm.label,
                    hour = alarm.hour,
                    minute = alarm.minute,
                    is24HourFormat = is24HourFormat,
                    scheduleId = alarm.id.toScheduleId()
                )
            alarmManagerHelper.schedule(pendingIntent, triggerTime)
        } else {
            alarm.repeatDays.forEach { dayOfWeek ->
                val triggerTime = calculateNextTriggerTime(dayOfWeek, alarm.hour, alarm.minute)
                val pendingIntent =
                    intentProvider.provideNotificationIntent(
                        alarmId = alarm.id,
                        label = alarm.label,
                        hour = alarm.hour,
                        minute = alarm.minute,
                        is24HourFormat = is24HourFormat,
                        scheduleId = alarm.id.toScheduleId(dayOfWeek)
                    )
                alarmManagerHelper.schedule(pendingIntent, triggerTime)
            }
        }
    }

    override fun cancel(alarm: Alarm) {
        alarmManagerHelper.cancel(
            intentProvider.provideNotificationIntent(
                alarmId = alarm.id,
                label = alarm.label,
                hour = alarm.hour,
                minute = alarm.minute,
                is24HourFormat = true,
                scheduleId = alarm.id.toScheduleId()
            )
        )
        for (dayOfWeek in DayOfWeek.entries) {
            val pendingIntent =
                intentProvider.provideNotificationIntent(
                    alarmId = alarm.id,
                    label = alarm.label,
                    hour = alarm.hour,
                    minute = alarm.minute,
                    is24HourFormat = true,
                    scheduleId = alarm.id.toScheduleId(dayOfWeek)
                )
            alarmManagerHelper.cancel(pendingIntent)
        }
    }

    override fun isScheduleNotificationAllowed(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun Long.toScheduleId(dayOfWeek: DayOfWeek): Int {
        return this.toInt() * 1000 + dayOfWeek.value
    }

    private fun Long.toScheduleId(): Int {
        return this.toInt() * 1000
    }
}