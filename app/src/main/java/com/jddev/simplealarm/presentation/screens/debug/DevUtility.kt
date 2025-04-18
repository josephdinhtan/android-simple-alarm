package com.jddev.simpletouch.ui.debug

import android.content.Context
import com.jddev.simplealarm.core.default
import com.jddev.simplealarm.data.di.CoroutineScopeIO
import com.jddev.simplealarm.data.service.AlarmRingingService
import com.jddev.simplealarm.domain.model.alarm.Alarm
import com.jddev.simplealarm.domain.system.NotificationController
import com.jddev.simpletouch.utils.logging.LogManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DevUtility @Inject constructor(
    private val notificationController: NotificationController,
    private val context: Context,
    @CoroutineScopeIO private val coroutineScopeIO: CoroutineScope,
) {
    fun showAlarmNotification() {
        coroutineScopeIO.launch {
            notificationController.showAlarmNotification(Alarm.default().copy(label = "Alarm test label"))
        }
    }

    fun startRingingForegroundService() {
        AlarmRingingService.start(context, 1)
    }
}