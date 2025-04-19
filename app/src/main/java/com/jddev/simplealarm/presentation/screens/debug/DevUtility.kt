package com.jddev.simplealarm.presentation.screens.debug

import android.content.Context
import com.jddev.simplealarm.core.default
import com.jddev.simplealarm.data.di.CoroutineScopeIO
import com.jddev.simplealarm.data.service.AlarmRingingService
import com.jddev.simplealarm.domain.model.alarm.Alarm
import com.jddev.simplealarm.domain.platform.AlarmScheduler
import com.jddev.simplealarm.domain.platform.NotificationScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

@Singleton
class DevUtility @Inject constructor(
    private val notificationController: NotificationScheduler,
    private val alarmScheduler: AlarmScheduler,
    private val context: Context,
    @CoroutineScopeIO private val coroutineScopeIO: CoroutineScope,
) {
    fun showAlarmNotification() {
        coroutineScopeIO.launch {
//            notificationController.showAlarmNotification(
//                Alarm.default().copy(label = "Alarm test label")
//            )
        }
    }

    fun startRingingForegroundService() {
        AlarmRingingService.startRinging(context, 1)
    }

    fun schedulePreAlarmNotificationAfter1Minutes() {
        val now = System.currentTimeMillis()
        val alarmTimeMillis = now + 60_000 // 1 minute from now

        val calendar = Calendar.getInstance().apply {
            timeInMillis = alarmTimeMillis
        }

        val alarm = Alarm.default().copy(
            hour = calendar.get(Calendar.HOUR_OF_DAY),
            minute = calendar.get(Calendar.MINUTE),
            id = 1,
            label = "Alarm schedule test label"
        )

        val notifyBeforeAt = (alarmTimeMillis - now - 5_000).milliseconds

        coroutineScopeIO.launch {
//            notificationController.schedulePreAlarmNotification(alarm, notifyBeforeAt)
        }
    }

    fun scheduleAlarmRinging() {
        val now = System.currentTimeMillis()
        val alarmTimeMillis = now + 60_000

        val calendar = Calendar.getInstance().apply {
            timeInMillis = alarmTimeMillis
        }

        coroutineScopeIO.launch {
            alarmScheduler.schedule(
                1,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE)
            )
        }
    }

    fun cancelSchedulePreAlarmNotification() {
        val alarm = Alarm.default().copy(id = 1, label = "Alarm schedule test label")
        coroutineScopeIO.launch {
//            notificationController.cancelPreAlarmNotification(alarm.id)
        }
    }

    fun cancelAllSchedulePreAlarmNotification() {
        coroutineScopeIO.launch {
//            notificationController.cancelAllPreAlarmNotifications()
        }
    }
}