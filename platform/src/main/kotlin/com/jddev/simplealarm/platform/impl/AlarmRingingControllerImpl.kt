package com.jddev.simplealarm.platform.impl

import android.content.Context
import com.jddev.simplealarm.platform.activity.AlarmRingingActivity
import com.jddev.simplealarm.platform.service.AlarmRingingService
import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.platform.AlarmRingingController
import javax.inject.Inject
import kotlin.time.Duration

class AlarmRingingControllerImpl @Inject constructor(
    private val context: Context,
) : AlarmRingingController {

    override fun startFiringAlarm(
        alarm: Alarm,
        is24h: Boolean,
        volumeFadeDuration: Duration,
        ringingTimeLimit: Duration,
    ) {
        AlarmRingingService.startRinging(
            context,
            alarm,
            is24h,
            volumeFadeDuration,
            ringingTimeLimit
        )
        // In Ringing case AlarmRingingActivity will be started from Notification.
    }

    override fun dismissAlarm(alarm: Alarm) {
        AlarmRingingService.dismissAlarm(context, alarm)
        AlarmRingingActivity.dismissAlarm(context)
    }

    override fun missedAlarm(alarm: Alarm) {
        AlarmRingingService.missedAlarm(context, alarm)
        AlarmRingingActivity.missedAlarm(context)
    }

    override fun snoozeAlarm(alarm: Alarm) {
        AlarmRingingService.snoozeAlarm(context, alarm)
        AlarmRingingActivity.snoozeAlarm(context)
    }
}