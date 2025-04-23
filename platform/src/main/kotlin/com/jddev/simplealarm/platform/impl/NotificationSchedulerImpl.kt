package com.jddev.simplealarm.platform.impl

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.jddev.simplealarm.platform.helper.AlarmIntentProvider
import com.jddev.simplealarm.platform.utils.calculateNextTriggerTime
import com.jddev.simplealarm.platform.utils.calculateTriggerTime
import com.jscoding.simplealarm.domain.entity.alarm.DayOfWeek
import com.jscoding.simplealarm.domain.platform.NotificationScheduler
import javax.inject.Inject

class NotificationSchedulerImpl @Inject constructor(
    private val alarmManagerHelper: com.jddev.simplealarm.platform.helper.AlarmManagerHelper,
    private val intentProvider: AlarmIntentProvider,
    private val context: Context,
) : NotificationScheduler {

    override fun schedule(alarmId: Long, hour: Int, minute: Int, is24HourFormat: Boolean) {
        val triggerTime = calculateTriggerTime(hour, minute)
        val pendingIntent =
            intentProvider.provideNotificationIntent(
                alarmId = alarmId,
                hour = hour,
                minute = minute,
                is24HourFormat = is24HourFormat,
                scheduleId = alarmId.toScheduleId()
            )
        alarmManagerHelper.schedule(pendingIntent, triggerTime)
    }

    override fun schedule(
        alarmId: Long,
        hour: Int,
        minute: Int,
        daysOfWeek: List<DayOfWeek>,
        is24HourFormat: Boolean,
    ) {
        daysOfWeek.forEach { dayOfWeek ->
            val triggerTime = calculateNextTriggerTime(dayOfWeek, hour, minute)
            val pendingIntent =
                intentProvider.provideNotificationIntent(
                    alarmId = alarmId,
                    hour = hour,
                    minute = minute,
                    is24HourFormat = is24HourFormat,
                    scheduleId = alarmId.toScheduleId(dayOfWeek)
                )
            alarmManagerHelper.schedule(pendingIntent, triggerTime)
        }
    }

    override fun cancel(alarmId: Long, hour: Int, minute: Int, is24HourFormat: Boolean) {
        alarmManagerHelper.cancel(
            intentProvider.provideNotificationIntent(
                alarmId = alarmId,
                hour = hour,
                minute = minute,
                is24HourFormat = is24HourFormat,
                scheduleId = alarmId.toScheduleId()
            )
        )
        for (dayOfWeek in DayOfWeek.entries) {
            val pendingIntent =
                intentProvider.provideNotificationIntent(
                    alarmId = alarmId,
                    hour = hour,
                    minute = minute,
                    is24HourFormat = is24HourFormat,
                    scheduleId = alarmId.toScheduleId(dayOfWeek)
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