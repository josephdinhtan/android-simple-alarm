package com.jddev.simplealarm.platform.impl

import com.jddev.simplealarm.platform.helper.AlarmIntentProvider
import com.jddev.simplealarm.platform.utils.calculateNextTriggerTime
import com.jddev.simplealarm.platform.utils.calculateTriggerTime
import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.entity.alarm.DayOfWeek
import com.jscoding.simplealarm.domain.platform.AlarmScheduler
import timber.log.Timber
import javax.inject.Inject

class AlarmSchedulerImpl @Inject constructor(
    private val alarmManagerHelper: com.jddev.simplealarm.platform.helper.AlarmManagerHelper,
    private val intentProvider: AlarmIntentProvider,
) : AlarmScheduler {

    override fun schedule(alarm: Alarm) {
        if(alarm.repeatDays.isEmpty()) {
            val triggerTime = calculateTriggerTime(alarm.hour, alarm.minute)
            val pendingIntent = intentProvider.provideAlarmIntent(alarm.id, alarm.id.toScheduleId())
            Timber.d("Alarm scheduled for id: ${alarm.id}, scheduleId: ${alarm.id.toScheduleId()}, time: ${alarm.hour}:${alarm.minute}")
            alarmManagerHelper.schedule(pendingIntent, triggerTime)
        } else {
            alarm.repeatDays.forEach { dayOfWeek ->
                val triggerTime = calculateNextTriggerTime(dayOfWeek, alarm.hour, alarm.minute)
                val pendingIntent =
                    intentProvider.provideAlarmIntent(alarm.id, alarm.id.toScheduleId(dayOfWeek))
                alarmManagerHelper.schedule(pendingIntent, triggerTime)
            }
        }
    }

    override fun cancel(alarm: Alarm) {
        alarmManagerHelper.cancel(
            intentProvider.provideAlarmIntent(
                alarm.id,
                alarm.id.toScheduleId()
            )
        )
        for (dayOfWeek in DayOfWeek.entries) {
            val pendingIntent =
                intentProvider.provideAlarmIntent(alarm.id, alarm.id.toScheduleId(dayOfWeek))
            alarmManagerHelper.cancel(pendingIntent)
        }
    }

    private fun Long.toScheduleId(dayOfWeek: DayOfWeek): Int {
        return this.toInt() * 10 + dayOfWeek.value
    }

    private fun Long.toScheduleId(): Int {
        return this.toInt() * 10
    }
}