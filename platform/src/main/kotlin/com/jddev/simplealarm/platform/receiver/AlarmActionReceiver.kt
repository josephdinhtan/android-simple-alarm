package com.jddev.simplealarm.platform.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jddev.simplealarm.platform.di.CoroutineScopeIO
import com.jddev.simplealarm.platform.dto.AlarmDto
import com.jddev.simplealarm.platform.mapper.toDomain
import com.jscoding.simplealarm.domain.usecase.alarm.DismissAlarmUseCase
import com.jscoding.simplealarm.domain.usecase.alarm.SnoozeAlarmUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AlarmActionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var snoozeUseCase: SnoozeAlarmUseCase

    @Inject
    lateinit var dismissUseCase: DismissAlarmUseCase

    @Inject
    @CoroutineScopeIO
    lateinit var coroutineScopeIo: CoroutineScope

    override fun onReceive(context: Context, intent: Intent) {
        val json = intent.getStringExtra(EXTRA_ALARM) ?: return
        val alarmDto = Json.decodeFromString<AlarmDto>(json)
        val alarm = alarmDto.toDomain()

        Timber.d("onReceive: ${intent.action}")

        when (intent.action) {
            ACTION_SNOOZE_ALARM_FROM_NOTIFICATION -> {
                coroutineScopeIo.launch {
                    snoozeUseCase(alarm)
                }
            }

            ACTION_DISMISS_ALARM_FROM_NOTIFICATION -> {
                coroutineScopeIo.launch {
                    dismissUseCase(alarm)
                }
            }
        }
    }

    companion object {
        const val EXTRA_ALARM = "alarm_dto"

        const val ACTION_DISMISS_ALARM_FROM_NOTIFICATION =
            "com.jddev.simplealarm.ACTION_DISMISS_ALARM_FROM_NOTIFICATION"
        const val ACTION_SNOOZE_ALARM_FROM_NOTIFICATION =
            "com.jddev.simplealarm.ACTION_SNOOZE_ALARM_FROM_NOTIFICATION"
    }
}