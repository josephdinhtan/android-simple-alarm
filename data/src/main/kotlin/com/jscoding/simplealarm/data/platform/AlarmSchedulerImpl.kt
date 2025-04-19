package com.jscoding.simplealarm.data.platform

import com.jscoding.simplealarm.data.helper.AlarmManagerHelper
import com.jscoding.simplealarm.data.helper.ScheduleType
import com.jscoding.simplealarm.data.utils.calculateNextTriggerTime
import com.jscoding.simplealarm.data.utils.calculateTriggerTime
import com.jscoding.simplealarm.domain.model.DayOfWeek
import com.jscoding.simplealarm.domain.platform.AlarmScheduler
import javax.inject.Inject

class AlarmSchedulerImpl @Inject constructor(
    private val alarmManagerHelper: AlarmManagerHelper,
) : AlarmScheduler {

    override fun schedule(alarmId: Long, hour: Int, minute: Int) {
        val triggerTime = calculateTriggerTime(hour, minute)
        alarmManagerHelper.schedule(
            alarmId.toScheduleId(),
            alarmId,
            triggerTime,
            ScheduleType.ALARM
        )
    }

    override fun schedule(alarmId: Long, hour: Int, minute: Int, daysOfWeek: List<DayOfWeek>) {
        daysOfWeek.forEach { dayOfWeek ->
            val triggerTime = calculateNextTriggerTime(dayOfWeek, hour, minute)
            alarmManagerHelper.schedule(
                alarmId.toScheduleId(dayOfWeek),
                alarmId,
                triggerTime,
                ScheduleType.ALARM
            )
        }
    }

    override fun cancel(alarmId: Long) {
        alarmManagerHelper.cancel(alarmId.toScheduleId())
        for (dayOfWeek in DayOfWeek.entries) {
            alarmManagerHelper.cancel(alarmId.toScheduleId(dayOfWeek))
        }
    }

    private fun Long.toScheduleId(dayOfWeek: DayOfWeek): Int {
        return this.toInt() * 10 + dayOfWeek.value
    }

    private fun Long.toScheduleId(): Int {
        return this.toInt() * 10
    }
}