package com.jddev.simplealarm.impl

import android.content.Context
import com.jddev.simplealarm.service.AlarmKlaxonService
import com.jscoding.simplealarm.domain.platform.AlarmRingingController
import javax.inject.Inject

class AlarmRingingControllerImpl @Inject constructor(
    private val context: Context,
) : AlarmRingingController {
    override fun dismissRinging(alarmId: Long) {
        AlarmKlaxonService.dismissAlarm(context, alarmId)
    }

    override fun snoozeRinging(alarmId: Long) {
        AlarmKlaxonService.snoozeAlarm(context, alarmId)
    }
}