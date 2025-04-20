package com.jddev.simplealarm.impl

import android.content.Context
import com.jddev.simplealarm.service.AlarmRingingService
import com.jscoding.simplealarm.domain.platform.AlarmRingingController
import javax.inject.Inject

class AlarmRingingControllerImpl @Inject constructor(
    private val context: Context,
) : AlarmRingingController {
    override fun dismissRinging() {
        AlarmRingingService.dismissAlarm(context)
    }

    override fun snoozeRinging() {
        AlarmRingingService.snoozeAlarm(context)
    }
}