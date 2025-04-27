package com.jddev.simplealarm.platform.impl

import android.content.Context
import com.jddev.simplealarm.platform.service.AlarmRingingService
import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.platform.AlarmRingingController
import javax.inject.Inject

class AlarmRingingControllerImpl @Inject constructor(
    private val context: Context,
) : AlarmRingingController {
    override fun dismissRinging(alarm: Alarm) {
        AlarmRingingService.dismissAlarm(context, alarm)
    }

    override fun snoozeRinging(alarm: Alarm) {
        AlarmRingingService.snoozeAlarm(context, alarm)
    }
}