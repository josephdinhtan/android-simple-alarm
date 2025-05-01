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
import com.jddev.simplealarm.platform.receiver.AlarmRingingReceiver
import com.jddev.simplealarm.platform.utils.calculateNextTriggerTime
import com.jddev.simplealarm.platform.utils.calculateTriggerTime
import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.entity.alarm.DayOfWeek
import com.jscoding.simplealarm.domain.platform.AlarmPreNotificationScheduler
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class NotificationSchedulerImpl @Inject constructor(
    private val alarmManagerHelper: AlarmManagerHelper,
    private val context: Context,
) : AlarmPreNotificationScheduler {

    override fun schedule(alarm: Alarm) {
        if (alarm.repeatDays.isEmpty()) {
            val triggerTime = calculateTriggerTime(alarm.hour, alarm.minute)
            val pendingIntent = provideNotificationIntent(
                context = context,
                alarmDto = alarm.toDto(),
                scheduleId = alarm.id.toScheduleId()
            )
            alarmManagerHelper.schedule(pendingIntent, triggerTime)
        } else {
            alarm.repeatDays.forEach { dayOfWeek ->
                val triggerTime = calculateNextTriggerTime(dayOfWeek, alarm.hour, alarm.minute)
                val pendingIntent = provideNotificationIntent(
                    context = context,
                    alarmDto = alarm.toDto(),
                    scheduleId = alarm.id.toScheduleId(dayOfWeek)
                )
                alarmManagerHelper.schedule(pendingIntent, triggerTime)
            }
        }
    }

    override fun cancel(alarm: Alarm) {
        alarmManagerHelper.cancel(
            provideNotificationIntent(
                context = context,
                alarmDto = alarm.toDto(),
                scheduleId = alarm.id.toScheduleId()
            )
        )
        for (dayOfWeek in DayOfWeek.entries) {
            val pendingIntent = provideNotificationIntent(
                context = context,
                alarmDto = alarm.toDto(),
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

    private fun provideNotificationIntent(
        context: Context,
        alarmDto: AlarmDto,
        scheduleId: Int,
    ): PendingIntent {
        val jsonAlarmDto = Json.encodeToString(alarmDto)
        val intent = Intent(context, AlarmRingingReceiver::class.java).apply {
            action = AlarmRingingReceiver.ACTION_FIRING_PRE_NOTIFICATION
            putExtra(AlarmRingingReceiver.EXTRA_ALARM, jsonAlarmDto)
        }
        return PendingIntent.getBroadcast(
            context,
            scheduleId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun Long.toScheduleId(dayOfWeek: DayOfWeek): Int {
        return this.toInt() * 100 + 51 + dayOfWeek.value
    }

    private fun Long.toScheduleId(): Int {
        return this.toInt() * 100 + 50
    }
}