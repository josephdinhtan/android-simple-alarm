package com.jddev.simplealarm.platform.impl

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.jddev.simplealarm.platform.dto.AlarmDto
import com.jddev.simplealarm.platform.helper.AlarmManagerHelper
import com.jddev.simplealarm.platform.mapper.toDto
import com.jddev.simplealarm.platform.receiver.AlarmRingingReceiver
import com.jddev.simplealarm.platform.utils.calculateNextTriggerTime
import com.jddev.simplealarm.platform.utils.calculateTriggerTime
import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.entity.alarm.DayOfWeek
import com.jscoding.simplealarm.domain.platform.AlarmScheduler
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject

class AlarmSchedulerImpl @Inject constructor(
    private val alarmManagerHelper: AlarmManagerHelper,
    private val context: Context,
) : AlarmScheduler {

    override fun schedule(alarm: Alarm) {
        if (alarm.repeatDays.isEmpty()) {
            val triggerTime = calculateTriggerTime(alarm.hour, alarm.minute)
            val pendingIntent = provideAlarmIntent(
                context,
                alarm.toDto(),
                alarm.id.toScheduleId()
            )
            Timber.d("Alarm scheduled for id: ${alarm.id}, scheduleId: ${alarm.id.toScheduleId()}, time: ${alarm.hour}:${alarm.minute}")
            alarmManagerHelper.schedule(pendingIntent, triggerTime)
        } else {
            alarm.repeatDays.forEach { dayOfWeek ->
                val triggerTime = calculateNextTriggerTime(dayOfWeek, alarm.hour, alarm.minute)
                val pendingIntent = provideAlarmIntent(
                    context,
                    alarm.toDto(),
                    alarm.id.toScheduleId(dayOfWeek)
                )
                Timber.d("Alarm scheduled for id: ${alarm.id}, scheduleId: ${alarm.id.toScheduleId()}, time: ${alarm.hour}:${alarm.minute}, dayOfWeek: $dayOfWeek")
                alarmManagerHelper.schedule(pendingIntent, triggerTime)
            }
        }
    }

    override fun cancel(alarm: Alarm) {
        alarmManagerHelper.cancel(
            provideAlarmIntent(
                context,
                alarm.toDto(),
                alarm.id.toScheduleId()
            )
        )

        Timber.d("Cancel Alarm id: ${alarm.id}, scheduleId: ${alarm.id.toScheduleId()}, time: ${alarm.hour}:${alarm.minute}")
        for (dayOfWeek in DayOfWeek.entries) {
            val pendingIntent = provideAlarmIntent(
                context,
                alarm.toDto(),
                alarm.id.toScheduleId(dayOfWeek)
            )
            alarmManagerHelper.cancel(pendingIntent)
        }
    }

    private fun provideAlarmIntent(
        context: Context,
        alarmDto: AlarmDto,
        scheduleId: Int,
    ): PendingIntent {
        val jsonAlarmDto = Json.encodeToString(alarmDto)
        val intent = Intent(context, AlarmRingingReceiver::class.java).apply {
            action = AlarmRingingReceiver.ACTION_FIRING_ALARM
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
        return this.toInt() * 100 + dayOfWeek.value + 1
    }

    private fun Long.toScheduleId(): Int {
        return this.toInt() * 100
    }
}