package com.jddev.simplealarm.debug

import android.content.Context
import android.widget.Toast
import com.jscoding.simplealarm.data.di.CoroutineScopeIO
import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.entity.alarm.NotificationType
import com.jscoding.simplealarm.domain.entity.alarm.Ringtone
import com.jscoding.simplealarm.domain.usecase.alarm.FiringAlarmUseCase
import com.jscoding.simplealarm.domain.usecase.alarm.TryScheduleNextAlarmUseCase
import com.jscoding.simplealarm.domain.usecase.others.ShowNotificationUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Singleton
class DevUtility @Inject constructor(
    private val showNotificationUseCase: ShowNotificationUseCase,
    private val ringingAlarmUseCase: FiringAlarmUseCase,
    private val tryScheduleNextAlarmUseCase: TryScheduleNextAlarmUseCase,
    private val context: Context,
    @CoroutineScopeIO private val coroutineScopeIO: CoroutineScope,
) {
    fun showAlarmSnoozeNotification() {
        coroutineScopeIO.launch {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
            }
            val snoozeAlarm = Alarm(
                hour = calendar.get(Calendar.HOUR_OF_DAY),
                minute = calendar.get(Calendar.MINUTE),
                repeatDays = emptyList(),
                preAlarmNotificationDuration = Duration.ZERO,
                enabled = true,
                label = "Alarm snoozed test label",
                id = 1,
                createdAt = System.currentTimeMillis(),
                ringtone = Ringtone.Silent,
                vibration = true,
                snoozeTime = 5.minutes
            )
            showNotificationUseCase(
                snoozeAlarm,
                "Alarm snoozed",
                NotificationType.ALARM_SNOOZE
            )
        }
    }

    fun startRingingActivity() {
        coroutineScopeIO.launch {
            ringingAlarmUseCase(Alarm.defaultTest())
        }
    }

    fun scheduleAlarmAndPreNotification() {
        coroutineScopeIO.launch {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
            }
            calendar.add(Calendar.MINUTE, 2)
            val alarm = Alarm.defaultTest().copy(
                hour = calendar.get(Calendar.HOUR_OF_DAY),
                minute = calendar.get(Calendar.MINUTE),
                preAlarmNotificationDuration = 1.minutes,
                enabled = true
            )
            tryScheduleNextAlarmUseCase(alarm)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Alarm scheduled at ${calendar.time}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

private fun Alarm.Companion.defaultTest(): Alarm {
    return Alarm(
        hour = 12,
        minute = 0,
        id = 1,
        label = "From Dev Panel",
        createdAt = 1,
        enabled = true,
        repeatDays = emptyList(),
        ringtone = Ringtone.Silent,
        vibration = true,
        snoozeTime = 5.minutes,
        preAlarmNotificationDuration = 5.minutes
    )
}