package com.jddev.simplealarm.platform.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.jddev.simplealarm.platform.dto.AlarmDto
import com.jddev.simplealarm.platform.helper.MediaPlayerHelper
import com.jddev.simplealarm.platform.helper.NotificationHelper
import com.jddev.simplealarm.platform.mapper.toDomain
import com.jddev.simplealarm.platform.mapper.toDto
import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.entity.alarm.NotificationAction
import com.jscoding.simplealarm.domain.entity.alarm.NotificationType
import com.jscoding.simplealarm.domain.platform.SystemSettingsManager
import com.jscoding.simplealarm.domain.usecase.alarm.MissedAlarmUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import javax.annotation.Nullable
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
internal class AlarmRingingService : LifecycleService() {

    @Inject
    lateinit var mediaPlayerHelper: MediaPlayerHelper

    @Inject
    lateinit var systemSettingsManager: SystemSettingsManager

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var missedAlarmUseCase: MissedAlarmUseCase

    @Inject
    @JvmField
    @field:Nullable
    var vibrator: Vibrator? = null

    private var alarmFiringTimeOutJob: Job? = null
    private var isAlarmRinging = false

    override fun onCreate() {
        super.onCreate()
        isAlarmRinging = false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Timber.d("onStartCommand: ${intent?.action}")
        intent ?: run {
            Timber.e("Intent is invalid, finish")
            stopSelf()
            return START_NOT_STICKY
        }

        val json = intent.getStringExtra(EXTRA_ALARM) ?: run {
            Timber.e("Alarm is invalid, finish")
            stopSelf()
            return START_NOT_STICKY
        }

        val intentAction = intent.action ?: run {
            Timber.e("Invalid alarm intent Action, finish")
            stopSelf()
            return START_NOT_STICKY
        }

        val alarmDto = Json.decodeFromString<AlarmDto>(json)
        val alarm = alarmDto.toDomain()

        return handleRequests(intentAction, alarm, intent)
    }

    private fun handleRequests(action: String, alarm: Alarm, intent: Intent) =
        when (action) {
            ACTION_DISMISS_ALARM, ACTION_SNOOZE_ALARM, ACTION_MISSED_ALARM -> {
                if (!isAlarmRinging) {
                    Timber.d("Alarm is not Ringing state, so ignore, finish")
                    stopSelf()
                } else {
                    stopAlarmRingtone()
                    cleanupAndFinishService()
                    isAlarmRinging = false
                }
                START_NOT_STICKY
            }

            ACTION_ALARM_RINGING -> {
                stopAlarmRingtone()

                val is24HourFormat = intent.getBooleanExtra(EXTRA_IS_24H, false)
                val volumeFadeDuration =
                    intent.getLongExtra(EXTRA_VOLUME_FADE_DURATION, 0)
                val ringingTimeLimit = intent.getLongExtra(EXTRA_RINGING_LIMIT_DURATION, 5)
                startRingingAndVibrateAlarm(
                    alarm,
                    volumeFadeDuration.seconds,
                    ringingTimeLimit.minutes
                )


                // update notification
                val notificationContent = getAlarmTimeDisplay(
                    hour = alarm.hour,
                    minutes = alarm.minute,
                    is24HourFormat
                )
                val notification = notificationHelper.createAlarmNotification(
                    alarm.label.ifEmpty { "Alarm" },
                    notificationContent,
                    alarm,
                    is24HourFormat,
                    NotificationType.ALARM_FIRING,
                    listOf(NotificationAction.SNOOZE, NotificationAction.DISMISS)
                )
                Timber.d("ACTION_ALARM_RINGING, show notification alarm label: ${alarm.label} id: ${alarm.id}, content: $notificationContent")
                isAlarmRinging = true
                startForeground(
                    getNotificationId(alarm.id), notification
                )
                START_STICKY
            }

            else -> {
                Timber.e("Invalid action: $action")
                START_NOT_STICKY
            }
        }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy")
        alarmFiringTimeOutJob?.cancel()
    }

    private fun missedAlarm(alarm: Alarm) {
        Timber.d("missedAlarm alarm: ${alarm.label}, id: ${alarm.id}")
        lifecycleScope.launch(Dispatchers.IO) {
            missedAlarmUseCase(alarm)
        }
    }

    private fun cleanupAndFinishService() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun stopAlarmRingtone() {
        Timber.d("stopRingingAlarm")
        mediaPlayerHelper.stop()
        vibrator?.cancel()
    }

    private fun startRingingAndVibrateAlarm(
        alarm: Alarm,
        volumeDuration: Duration,
        ringingTimeLimit: Duration,
    ) {
        // Play sound
        if (alarm.ringtone.uri != Uri.EMPTY) {
            val currentVolume = systemSettingsManager.getAlarmVolume().toFloat()
            val maxVolume = systemSettingsManager.getMaxAlarmVolume().toFloat()
            val volume = if (maxVolume == 0f) 1f else currentVolume / maxVolume

            mediaPlayerHelper.play(
                alarm.ringtone.uri, volume, volumeDuration
            )
        }

        // Vibration
        if (alarm.vibration) {
            vibrator?.vibrate(
                VibrationEffect.createWaveform(longArrayOf(0, 500, 500), 0)
            )
        }
        // Wake up screen
        wakeUpScreen(this@AlarmRingingService.applicationContext)

        // Timeout monitor
        alarmFiringTimeOutJob?.cancel()
        if(ringingTimeLimit.inWholeMinutes < 0) {
            Timber.d("If there no action, it'll ringing forever, no limit")
        } else {
            alarmFiringTimeOutJob = lifecycleScope.launch(Dispatchers.IO) {
                delay(ringingTimeLimit.inWholeMinutes.seconds)
                missedAlarm(alarm)
                delay(100)
                stopAlarmRingtone()
                cleanupAndFinishService()
            }
        }
    }

    private fun wakeUpScreen(context: Context) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE,
            "SimpleAlarm:AlarmWakeLock"
        )
        wakeLock.acquire(3000L) // 3 seconds just to turn on the screen
    }

    private fun getAlarmTimeDisplay(
        hour: Int,
        minutes: Int,
        is24HourFormat: Boolean,
    ): String {
        val now = LocalDateTime.now()
        var baseTime = now.withHour(hour).withMinute(minutes).withSecond(0).withNano(0)

        if (baseTime.isBefore(now)) {
            baseTime = baseTime.plusDays(1)
        }

        val dayOfWeek = baseTime.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        val timeFormatter = if (is24HourFormat) {
            DateTimeFormatter.ofPattern("HH:mm")
        } else {
            DateTimeFormatter.ofPattern("hh:mm a")
        }

        return "$dayOfWeek ${baseTime.format(timeFormatter)}"
    }

    private fun getNotificationId(alarmId: Long): Int {
        return (alarmId * 100 + 10).toInt()
    }

    companion object {
        const val EXTRA_ALARM = "alarm_dto"
        const val EXTRA_IS_24H = "is_24h"
        const val EXTRA_VOLUME_FADE_DURATION = "volume_fade_duration"
        const val EXTRA_RINGING_LIMIT_DURATION = "ringing_limit_duration"

        const val ACTION_ALARM_RINGING = "com.jddev.simplealarm.ACTION_ALARM_RINGING"
        const val ACTION_DISMISS_ALARM = "com.jddev.simplealarm.ACTION_DISMISS_ALARM"
        const val ACTION_MISSED_ALARM = "com.jddev.simplealarm.ACTION_MISSED_ALARM"
        const val ACTION_SNOOZE_ALARM = "com.jddev.simplealarm.ACTION_SNOOZE_ALARM"

        internal fun startRinging(
            context: Context,
            alarm: Alarm,
            is24h: Boolean,
            volumeFadeDuration: Duration,
            ringingTimeLimit: Duration,
        ) {
            val jsonAlarmDto = Json.encodeToString(alarm.toDto())
            val intent = Intent(context, AlarmRingingService::class.java).apply {
                action = ACTION_ALARM_RINGING
                putExtra(EXTRA_ALARM, jsonAlarmDto)
                putExtra(EXTRA_IS_24H, is24h)
                putExtra(
                    EXTRA_VOLUME_FADE_DURATION,
                    volumeFadeDuration.inWholeSeconds
                )
                putExtra(
                    EXTRA_RINGING_LIMIT_DURATION,
                    ringingTimeLimit.inWholeMinutes
                )
            }
            ContextCompat.startForegroundService(context, intent)
        }

        internal fun dismissAlarm(context: Context, alarm: Alarm) {
            val jsonAlarmDto = Json.encodeToString(alarm.toDto())
            val intent = Intent(context, AlarmRingingService::class.java).apply {
                action = ACTION_DISMISS_ALARM
                putExtra(EXTRA_ALARM, jsonAlarmDto)
            }
            context.startService(intent)
        }

        internal fun missedAlarm(context: Context, alarm: Alarm) {
            val jsonAlarmDto = Json.encodeToString(alarm.toDto())
            val intent = Intent(context, AlarmRingingService::class.java).apply {
                action = ACTION_MISSED_ALARM
                putExtra(EXTRA_ALARM, jsonAlarmDto)
            }
            context.startService(intent)
        }

        internal fun snoozeAlarm(context: Context, alarm: Alarm) {
            val jsonAlarmDto = Json.encodeToString(alarm.toDto())
            val intent = Intent(context, AlarmRingingService::class.java).apply {
                action = ACTION_SNOOZE_ALARM
                putExtra(EXTRA_ALARM, jsonAlarmDto)
            }
            ContextCompat.startForegroundService(context, intent)
        }
    }
}