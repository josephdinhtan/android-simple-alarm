package com.jddev.simplealarm.platform.helper

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import javax.inject.Inject

enum class ScheduleType(val value: String) {
    ALARM("alarm"), NOTIFICATION("notification")
}

class AlarmIntentProvider @Inject constructor(
    private val context: Context,
) {
    fun provideAlarmIntent(alarmId: Long, scheduleId: Int): PendingIntent {
        val intent = Intent(context, com.jddev.simplealarm.platform.receiver.AlarmReceiver::class.java).apply {
            putExtra(EXTRA_TYPE, ScheduleType.ALARM.value)
            putExtra(EXTRA_ALARM_ID, alarmId)
        }
        return PendingIntent.getBroadcast(
            context,
            scheduleId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun provideNotificationIntent(
        alarmId: Long,
        hour: Int,
        minute: Int,
        is24HourFormat: Boolean,
        scheduleId: Int,
    ): PendingIntent {
        val intent = Intent(context, com.jddev.simplealarm.platform.receiver.AlarmReceiver::class.java).apply {
            putExtra(EXTRA_TYPE, ScheduleType.NOTIFICATION.value)
            putExtra(EXTRA_ALARM_ID, alarmId)
            putExtra(EXTRA_HOUR, hour)
            putExtra(EXTRA_MINUTE, minute)
            putExtra(EXTRA_IS_24H, is24HourFormat)
        }
        return PendingIntent.getBroadcast(
            context,
            scheduleId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        const val EXTRA_NOTIFICATION_ID = "notification_id"
        const val EXTRA_ALARM_ID = "alarm_id"
        const val EXTRA_IS_24H = "is_24h"
        const val EXTRA_HOUR = "hour"
        const val EXTRA_MINUTE = "minute"
        const val EXTRA_TYPE = "type"
    }
}