package com.jddev.simplealarm.platform.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jddev.simplealarm.platform.di.CoroutineScopeMain
import com.jddev.simplealarm.platform.helper.AlarmIntentProvider
import com.jddev.simplealarm.platform.helper.ScheduleType
import com.jddev.simplealarm.platform.service.AlarmKlaxonService
import com.jscoding.simplealarm.domain.entity.alarm.NotificationType
import com.jscoding.simplealarm.domain.usecase.alarm.ShowNotificationUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

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
                AlarmKlaxonService.startRinging(context, alarmId)
            }

            ScheduleType.NOTIFICATION.value -> {
                val hour = intent.getIntExtra(AlarmIntentProvider.EXTRA_HOUR, 0)
                val minute = intent.getIntExtra(AlarmIntentProvider.EXTRA_MINUTE, 0)
                val is24HourFormat = intent.getBooleanExtra(AlarmIntentProvider.EXTRA_IS_24H, true)
                Timber.d("Show notification id: $alarmId, hour: $hour, minute: $minute, is24HourFormat: $is24HourFormat")

                coroutineScope.launch(NonCancellable) {
                    showNotificationUseCase(
                        alarmId.toInt(),
                        alarmId, hour, minute, NotificationType.ALARM_UPCOMING
                    )
                }
            }
        }
    }
}