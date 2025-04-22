package com.jddev.simplealarm.impl

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.jddev.simplealarm.receiver.AlarmReceiver
import com.jscoding.simplealarm.domain.platform.AlarmIntentProvider
import javax.inject.Inject

enum class ScheduleType(val value: String) {
    ALARM("alarm"), NOTIFICATION("notification")
}

class AlarmIntentProviderImpl @Inject constructor(
    private val context: Context,
) : AlarmIntentProvider {
    override fun provideAlarmIntent(alarmId: Long, scheduleId: Int): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("type", ScheduleType.ALARM.value)
            putExtra("alarmId", alarmId)
        }
        return PendingIntent.getBroadcast(
            context,
            scheduleId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun provideNotificationIntent(
        alarmId: Long,
        hour: Int,
        minute: Int,
        is24HourFormat: Boolean,
        scheduleId: Int,
    ): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("type", ScheduleType.NOTIFICATION.value)
            putExtra("alarmId", alarmId)
            putExtra("hour", hour)
            putExtra("minute", minute)
            putExtra("is24HourFormat", is24HourFormat)
        }
        return PendingIntent.getBroadcast(
            context,
            scheduleId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}