package com.jddev.simplealarm.debug

import android.content.Context
import com.jscoding.simplealarm.data.di.CoroutineScopeIO
import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.entity.alarm.NotificationType
import com.jscoding.simplealarm.domain.entity.alarm.Ringtone
import com.jscoding.simplealarm.domain.usecase.alarm.RingingAlarmUseCase
import com.jscoding.simplealarm.domain.usecase.others.ShowNotificationUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Singleton
class DevUtility @Inject constructor(
    private val showNotificationUseCase: ShowNotificationUseCase,
    private val ringingAlarmUseCase: RingingAlarmUseCase,
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
}

private fun Alarm.Companion.defaultTest(): Alarm {
    return Alarm(
        hour = 12,
        minute = 0,
        id = 1,
        label = "Test label",
        createdAt = 1,
        enabled = true,
        repeatDays = emptyList(),
        ringtone = Ringtone.Silent,
        vibration = true,
        snoozeTime = 5.minutes,
        preAlarmNotificationDuration = 5.minutes
    )
}