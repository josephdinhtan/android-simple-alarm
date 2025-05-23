package com.jddev.simplealarm.platform.impl

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.jddev.simplealarm.platform.dto.AlarmDto
import com.jddev.simplealarm.platform.helper.AlarmManagerHelper
import com.jddev.simplealarm.platform.mapper.toDto
import com.jddev.simplealarm.platform.receiver.AlarmScheduledReceiver
import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.platform.AlarmScheduler
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class AlarmSchedulerImpl @Inject constructor(
    private val alarmManagerHelper: AlarmManagerHelper,
    private val context: Context,
) : AlarmScheduler {

    override fun schedule(alarm: Alarm, triggerAt: LocalDateTime) {
        val triggerAtMillis = triggerAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val pendingIntent = provideAlarmIntent(
            context,
            alarm.toDto(),
            alarm.id.toScheduleId()
        )
        Timber.d("Alarm scheduled for id: ${alarm.id}, scheduleId: ${alarm.id.toScheduleId()}, at: ${triggerAt.toLocalTime()} - ${triggerAt.toLocalDate()}")
        alarmManagerHelper.schedule(pendingIntent, triggerAtMillis)
    }

    override fun cancel(alarm: Alarm) {
        Timber.d("Cancel Alarm scheduled for id: ${alarm.id}, scheduleId: ${alarm.id.toScheduleId()}, time: ${alarm.hour}:${alarm.minute}")
        alarmManagerHelper.cancel(
            provideAlarmIntent(
                context,
                alarm.toDto(),
                alarm.id.toScheduleId()
            )
        )
    }

    private fun provideAlarmIntent(
        context: Context,
        alarmDto: AlarmDto,
        scheduleId: Int,
    ): PendingIntent {
        val jsonAlarmDto = Json.encodeToString(alarmDto)
        val intent = Intent(context, AlarmScheduledReceiver::class.java).apply {
            action = AlarmScheduledReceiver.ACTION_FIRING_ALARM
            putExtra(AlarmScheduledReceiver.EXTRA_ALARM, jsonAlarmDto)
        }
        return PendingIntent.getBroadcast(
            context,
            scheduleId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

//    private fun Long.toScheduleId(dayOfWeek: DayOfWeek): Int {
//        return this.toInt() * 100 + dayOfWeek.value + 1
//    }

    private fun Long.toScheduleId(): Int {
        return this.toInt() * 100
    }
}