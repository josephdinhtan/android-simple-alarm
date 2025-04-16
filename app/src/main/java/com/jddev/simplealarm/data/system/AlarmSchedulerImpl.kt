package com.jddev.simplealarm.data.system

import com.jddev.simplealarm.data.helper.AlarmManagerHelper
import com.jddev.simplealarm.data.helper.ScheduleType
import com.jddev.simplealarm.data.utils.calculateTriggerTime
import com.jddev.simplealarm.domain.model.alarm.Alarm
import com.jddev.simplealarm.domain.system.AlarmScheduler
import javax.inject.Inject

class AlarmSchedulerImpl @Inject constructor(
    private val alarmManagerHelper: AlarmManagerHelper
) : AlarmScheduler {

    override fun schedule(alarm: Alarm) {
        val triggerTime = calculateTriggerTime(alarm.hour, alarm.minute)
        alarmManagerHelper.schedule(alarm, triggerTime, ScheduleType.ALARM)
    }

    override fun cancel(alarm: Alarm) {
        alarmManagerHelper.cancel(alarm)
    }
}