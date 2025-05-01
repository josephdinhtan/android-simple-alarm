package com.jddev.simplealarm.platform.impl

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.jddev.simplealarm.platform.dto.AlarmDto
import com.jddev.simplealarm.platform.helper.AlarmManagerHelper
import com.jddev.simplealarm.platform.mapper.toDto
import com.jddev.simplealarm.platform.receiver.AlarmScheduledReceiver
import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.platform.AlarmPreNotificationScheduler
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class NotificationSchedulerImpl @Inject constructor(
    private val alarmManagerHelper: AlarmManagerHelper,
    private val context: Context,
) : AlarmPreNotificationScheduler {

    override fun schedule(alarm: Alarm, triggerAt: LocalDateTime) {
        val triggerAtMillis = triggerAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val pendingIntent = provideNotificationIntent(
            context = context,
            alarmDto = alarm.toDto(),
            scheduleId = alarm.id.toScheduleId()
        )
        Timber.d("Notification scheduled for id: ${alarm.id}, scheduleId: ${alarm.id.toScheduleId()}, at: ${triggerAt.toLocalTime()}")
        alarmManagerHelper.schedule(pendingIntent, triggerAtMillis)
    }

    override fun cancel(alarm: Alarm) {
        Timber.d("Cancel Notification alarm id: ${alarm.id}, scheduleId: ${alarm.id.toScheduleId()}, time: ${alarm.hour}:${alarm.minute}")
        alarmManagerHelper.cancel(
            provideNotificationIntent(
                context = context,
                alarmDto = alarm.toDto(),
                scheduleId = alarm.id.toScheduleId()
            )
        )
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

    private fun provideNotificationIntent(
        context: Context,
        alarmDto: AlarmDto,
        scheduleId: Int,
    ): PendingIntent {
        val jsonAlarmDto = Json.encodeToString(alarmDto)
        val intent = Intent(context, AlarmScheduledReceiver::class.java).apply {
            action = AlarmScheduledReceiver.ACTION_FIRING_PRE_NOTIFICATION
            putExtra(AlarmScheduledReceiver.EXTRA_ALARM, jsonAlarmDto)
        }
        return PendingIntent.getBroadcast(
            context,
            scheduleId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun Long.toScheduleId(): Int {
        return this.toInt() * 100 + 50
    }
}