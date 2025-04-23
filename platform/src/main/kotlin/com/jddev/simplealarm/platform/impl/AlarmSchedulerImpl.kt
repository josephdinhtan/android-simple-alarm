package com.jddev.simplealarm.platform.impl

import com.jddev.simplealarm.platform.helper.AlarmIntentProvider
import com.jddev.simplealarm.platform.utils.calculateNextTriggerTime
import com.jddev.simplealarm.platform.utils.calculateTriggerTime
import com.jscoding.simplealarm.domain.entity.alarm.DayOfWeek
import com.jscoding.simplealarm.domain.platform.AlarmScheduler
import timber.log.Timber
import javax.inject.Inject

class AlarmSchedulerImpl @Inject constructor(
    private val alarmManagerHelper: com.jddev.simplealarm.platform.helper.AlarmManagerHelper,
    private val intentProvider: AlarmIntentProvider,
) : AlarmScheduler {

    override fun schedule(alarmId: Long, hour: Int, minute: Int) {
        val triggerTime = calculateTriggerTime(hour, minute)
        val pendingIntent = intentProvider.provideAlarmIntent(alarmId, alarmId.toScheduleId())
        Timber.d("Alarm scheduled for $hour:$minute")
        alarmManagerHelper.schedule(pendingIntent, triggerTime)
    }

    override fun schedule(alarmId: Long, hour: Int, minute: Int, daysOfWeek: List<DayOfWeek>) {
        daysOfWeek.forEach { dayOfWeek ->
            val triggerTime = calculateNextTriggerTime(dayOfWeek, hour, minute)
            val pendingIntent =
                intentProvider.provideAlarmIntent(alarmId, alarmId.toScheduleId(dayOfWeek))
            alarmManagerHelper.schedule(pendingIntent, triggerTime)
        }
    }

    override fun cancel(alarmId: Long) {
        alarmManagerHelper.cancel(
            intentProvider.provideAlarmIntent(
                alarmId,
                alarmId.toScheduleId()
            )
        )
        for (dayOfWeek in DayOfWeek.entries) {
            val pendingIntent =
                intentProvider.provideAlarmIntent(alarmId, alarmId.toScheduleId(dayOfWeek))
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