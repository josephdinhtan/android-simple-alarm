package com.jddev.simplealarm.platform.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import com.jddev.simplealarm.platform.di.CoroutineScopeIO
import com.jddev.simplealarm.platform.dto.AlarmDto
import com.jddev.simplealarm.platform.mapper.toDomain
import com.jscoding.simplealarm.domain.entity.alarm.NotificationType
import com.jscoding.simplealarm.domain.usecase.alarm.FiringAlarmUseCase
import com.jscoding.simplealarm.domain.usecase.others.ShowNotificationUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
internal class AlarmScheduledReceiver : BroadcastReceiver() {

    @Inject
    lateinit var showNotificationUseCase: ShowNotificationUseCase

    @Inject
    lateinit var firingAlarmUseCase: FiringAlarmUseCase

    @Inject
    @CoroutineScopeIO
    lateinit var coroutineScopeIo: CoroutineScope

    override fun onReceive(context: Context, intent: Intent) {
        val json = intent.getStringExtra(EXTRA_ALARM) ?: return
        val alarmDto = Json.decodeFromString<AlarmDto>(json)
        val alarm = alarmDto.toDomain()

        Timber.d("Received intent action: ${intent.action}, alarm: $alarm")

        when (intent.action) {
            ACTION_FIRING_ALARM -> {
                coroutineScopeIo.launch(NonCancellable) {
                    firingAlarmUseCase(alarm)
                }
            }

            ACTION_FIRING_PRE_NOTIFICATION -> {
                wakeUpScreen(context, 3.seconds)
                coroutineScopeIo.launch(NonCancellable) {
                    showNotificationUseCase(
                        alarm = alarm,
                        title = "Upcoming alarm",
                        NotificationType.ALARM_UPCOMING
                    )
                }
            }
        }
    }

    private fun wakeUpScreen(context: Context, duration: Duration) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_DIM_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "alarm:AlarmWakeLock"
        )
        wakeLock.acquire(duration.inWholeMilliseconds)
    }

    companion object {
        const val EXTRA_ALARM = "alarm_dto"
        const val ACTION_FIRING_ALARM =
            "com.jddev.simplealarm.ACTION_FIRING_ALARM"
        const val ACTION_FIRING_PRE_NOTIFICATION =
            "com.jddev.simplealarm.ACTION_FIRING_PRE_NOTIFICATION"
    }
}