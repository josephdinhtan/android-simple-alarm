package com.jddev.simplealarm.platform.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jddev.simplealarm.platform.di.CoroutineScopeMain
import com.jddev.simplealarm.platform.helper.AlarmIntentProvider
import com.jddev.simplealarm.platform.helper.ScheduleType
import com.jddev.simplealarm.platform.service.AlarmRingingService
import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.entity.alarm.NotificationType
import com.jscoding.simplealarm.domain.entity.alarm.Ringtone
import com.jscoding.simplealarm.domain.usecase.alarm.ShowNotificationUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var showNotificationUseCase: ShowNotificationUseCase

    @Inject
    @CoroutineScopeMain
    lateinit var coroutineScope: CoroutineScope

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra(AlarmIntentProvider.EXTRA_ALARM_ID, -1)
        val style = intent.getStringExtra(AlarmIntentProvider.EXTRA_TYPE)

        Timber.d("Received intent alarmId: $alarmId, type: $style")
        when (style) {
            ScheduleType.ALARM.value -> {
                AlarmRingingService.startRinging(context, alarmId)
            }

            ScheduleType.NOTIFICATION.value -> {
                val hour = intent.getIntExtra(AlarmIntentProvider.EXTRA_HOUR, 0)
                val label = intent.getStringExtra(AlarmIntentProvider.EXTRA_LABEL)
                val minute = intent.getIntExtra(AlarmIntentProvider.EXTRA_MINUTE, 0)
                val is24HourFormat = intent.getBooleanExtra(AlarmIntentProvider.EXTRA_IS_24H, true)
                Timber.d("Show notification id: $alarmId, hour: $hour, minute: $minute, is24HourFormat: $is24HourFormat")

                val alarmForNotification = Alarm(
                    id = alarmId,
                    label = label ?: "",
                    hour = hour,
                    minute = minute,
                    repeatDays = emptyList(),
                    preAlarmNotificationDuration = Duration.ZERO,
                    enabled = true,
                    snoozeTime = Duration.ZERO,
                    ringtone = Ringtone.Silent,
                    vibration = false,
                    createdAt = 0,
                )
                coroutineScope.launch(NonCancellable) {
                    showNotificationUseCase(
                        alarm = alarmForNotification,
                        title = "Upcoming alarm",
                        NotificationType.ALARM_UPCOMING
                    )
                }
            }
        }
    }
}