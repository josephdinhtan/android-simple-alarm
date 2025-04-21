package com.jddev.simplealarm.impl

import android.content.Context
import com.jddev.simplealarm.service.AlarmKlaxonService
import com.jscoding.simplealarm.domain.platform.AlarmRingingController
import javax.inject.Inject

class AlarmRingingControllerImpl @Inject constructor(
    private val context: Context,
) : AlarmRingingController {
    override fun dismissRinging() {
        AlarmKlaxonService.dismissAlarm(context)
    }

    override fun snoozeRinging() {
        AlarmKlaxonService.snoozeAlarm(context)
    }
}