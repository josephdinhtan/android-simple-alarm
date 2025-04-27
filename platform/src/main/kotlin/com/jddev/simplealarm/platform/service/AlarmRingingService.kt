package com.jddev.simplealarm.platform.service

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.jddev.simplealarm.platform.activity.RingingActivity
import com.jddev.simplealarm.platform.helper.AlarmIntentProvider.Companion.EXTRA_ALARM_ID
import com.jddev.simplealarm.platform.helper.MediaPlayerHelper
import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.entity.alarm.NotificationAction
import com.jscoding.simplealarm.domain.entity.alarm.NotificationType
import com.jscoding.simplealarm.domain.platform.SystemSettingsManager
import com.jscoding.simplealarm.domain.repository.AlarmRepository
import com.jscoding.simplealarm.domain.repository.SettingsRepository
import com.jscoding.simplealarm.domain.usecase.alarm.DismissAlarmUseCase
import com.jscoding.simplealarm.domain.usecase.alarm.SnoozeAlarmUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale
import javax.annotation.Nullable
import javax.inject.Inject

@AndroidEntryPoint
class AlarmRingingService : LifecycleService() {

    @Inject
    lateinit var mediaPlayerHelper: MediaPlayerHelper

    @Inject
    lateinit var alarmRepository: AlarmRepository

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var systemSettingsManager: SystemSettingsManager

    @Inject
    lateinit var notificationHelper: com.jddev.simplealarm.platform.helper.NotificationHelper

    @Inject
    @JvmField
    @field:Nullable
    var vibrator: Vibrator? = null

    // Use cases
    @Inject
    lateinit var dismissAlarmUseCase: DismissAlarmUseCase

    @Inject
    lateinit var snoozeAlarmUseCase: SnoozeAlarmUseCase

    private lateinit var notificationManager: NotificationManager

    private var isForegroundStarted = false

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Timber.d("onStartCommand: ${intent?.action}")

        startForegroundPlaceHolder()
        intent ?: run {
            Timber.e("Intent is invalid, finish")
            stopSelf()
            return START_NOT_STICKY
        }

        val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, -1)
        val intentAction = intent.action

        if (alarmId == -1L || intentAction == null) {
            Timber.e("Invalid alarm ID")
            stopSelf()
            return START_NOT_STICKY
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val alarm = alarmRepository.getAlarmById(alarmId) ?: run {
                Timber.e("Alarm not found for ID $alarmId")
                stopSelf()
                return@launch
            }
            handleRequests(intentAction, alarm)
        }

        return START_STICKY
    }

    private suspend fun handleRequests(action: String, alarm: Alarm) {
        when (action) {

            ACTION_DISMISS_ALARM, ACTION_SNOOZE_ALARM -> {
                notificationHelper.cancelNotification(alarm.id.toInt())
                stopAlarmRingtone()
                cleanupAndFinishService()
            }

            ACTION_DISMISS_ALARM_FROM_NOTIFICATION -> {
                RingingActivity.dismissActivity(this.applicationContext)
                dismissAlarmUseCase(alarm)
            }

            ACTION_SNOOZE_ALARM_FROM_NOTIFICATION -> {
                RingingActivity.dismissActivity(this.applicationContext)
                snoozeAlarmUseCase(alarm)
            }

            ACTION_ALARM_RINGING -> {
                notificationHelper.cancelNotification(alarm.id.toInt())
                stopAlarmRingtone()
                startRingingAndVibrateAlarm(alarm)
                // update notification
                val is24HourFormat = settingsRepository.getIs24HourFormat()
                val notificationTitle = "Alarm"
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = System.currentTimeMillis()
                }
                val notificationContent = getAlarmTimeDisplay(
                    hour = calendar.get(Calendar.HOUR_OF_DAY),
                    minutes = calendar.get(Calendar.MINUTE),
                    is24HourFormat
                )
                val notification = notificationHelper.createAlarmNotification(
                    notificationTitle,
                    notificationContent,
                    alarm.id,
                    NotificationType.ALARM_FIRING,
                    listOf(NotificationAction.SNOOZE, NotificationAction.DISMISS)
                )
                Timber.d("ACTION_ALARM_RINGING, show notification alarm label: ${alarm.label} id: ${alarm.id}, title: $notificationTitle, content: $notificationContent")
                startForeground(
                    com.jddev.simplealarm.platform.helper.NotificationHelper.CHANNEL_ALARM_FOREGROUND_NOTIFICATION_ID,
                    notification
                )
            }

            else -> {
                Timber.e("Invalid action: $action")
            }
        }
    }

    private fun startForegroundPlaceHolder() {
        if (isForegroundStarted) return
        isForegroundStarted = true
        val placeholderNotification =
            notificationHelper.createTemporaryRingingNotification(this.applicationContext)
        startForeground(
            com.jddev.simplealarm.platform.helper.NotificationHelper.CHANNEL_ALARM_FOREGROUND_NOTIFICATION_ID,
            placeholderNotification
        )
    }

    private fun missedAlarm() {
        // show missedAlarm notification
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

    private suspend fun startRingingAndVibrateAlarm(alarm: Alarm) {
        withContext(Dispatchers.Main) {
            // Play sound
            if (alarm.ringtone.uri != Uri.EMPTY) {
                val currentVolume = systemSettingsManager.getAlarmVolume().toFloat()
                val maxVolume = systemSettingsManager.getMaxAlarmVolume().toFloat()
                val volume = if (maxVolume == 0f) 1f else currentVolume / maxVolume

                mediaPlayerHelper.play(
                    alarm.ringtone.uri,
                    volume,
                    settingsRepository.getVolumeFadeDuration()
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
        }
    }

    private fun wakeUpScreen(context: Context) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.FULL_WAKE_LOCK or
                    PowerManager.ACQUIRE_CAUSES_WAKEUP or
                    PowerManager.ON_AFTER_RELEASE,
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

    companion object {
        const val ACTION_DISMISS_ALARM = "com.jddev.simplealarm.ACTION_DISMISS_ALARM"
        const val ACTION_DISMISS_ALARM_FROM_NOTIFICATION =
            "com.jddev.simplealarm.ACTION_DISMISS_ALARM_FROM_NOTIFICATION"
        const val ACTION_SNOOZE_ALARM = "com.jddev.simplealarm.ACTION_SNOOZE_ALARM"
        const val ACTION_SNOOZE_ALARM_FROM_NOTIFICATION =
            "com.jddev.simplealarm.ACTION_SNOOZE_ALARM_FROM_NOTIFICATION"
        const val ACTION_ALARM_RINGING = "com.jddev.simplealarm.ACTION_ALARM_RINGING"

        fun startRinging(context: Context, alarmId: Long) {
            val intent = Intent(context, AlarmRingingService::class.java).apply {
                action = ACTION_ALARM_RINGING
                putExtra(EXTRA_ALARM_ID, alarmId)
            }
            ContextCompat.startForegroundService(context, intent)
        }

        fun dismissAlarm(context: Context, alarm: Alarm) {
            val intent = Intent(context, AlarmRingingService::class.java).apply {
                action = ACTION_DISMISS_ALARM
                putExtra(EXTRA_ALARM_ID, alarm.id)
            }
            ContextCompat.startForegroundService(context, intent)
        }

        fun snoozeAlarm(context: Context, alarm: Alarm) {
            val intent = Intent(context, AlarmRingingService::class.java).apply {
                action = ACTION_SNOOZE_ALARM
                putExtra(EXTRA_ALARM_ID, alarm.id)
            }
            ContextCompat.startForegroundService(context, intent)
        }
    }
}