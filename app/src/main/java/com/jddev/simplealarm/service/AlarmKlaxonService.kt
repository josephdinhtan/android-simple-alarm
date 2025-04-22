package com.jddev.simplealarm.service

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.jddev.simplealarm.activity.RingingActivity
import com.jddev.simplealarm.helper.NotificationHelper
import com.jscoding.simplealarm.data.helper.MediaPlayerHelper
import com.jscoding.simplealarm.data.utils.getAlarmTimeDisplay
import com.jscoding.simplealarm.domain.model.alarm.Alarm
import com.jscoding.simplealarm.domain.platform.SystemSettingsManager
import com.jscoding.simplealarm.domain.repository.AlarmRepository
import com.jscoding.simplealarm.domain.repository.SettingsRepository
import com.jscoding.simplealarm.domain.usecase.alarm.DismissAlarmUseCase
import com.jscoding.simplealarm.domain.usecase.alarm.SnoozeAlarmUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AlarmKlaxonService : LifecycleService() {

    @Inject
    lateinit var mediaPlayerHelper: MediaPlayerHelper

    @Inject
    lateinit var alarmRepository: AlarmRepository

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var systemSettingsManager: SystemSettingsManager

    @Inject
    lateinit var notificationHelper: NotificationHelper

    // Use cases
    @Inject
    lateinit var dismissAlarmUseCase: DismissAlarmUseCase

    @Inject
    lateinit var snoozeAlarmUseCase: SnoozeAlarmUseCase

    private lateinit var notificationManager: NotificationManager

    private var isForegroundStarted = false
    private var currentAlarm: Alarm? = null

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Timber.d("onStartCommand: ${intent?.action}")

        startForegroundPlaceHolder()
        if (intent == null || intent.action.isNullOrEmpty()) {
            Timber.e("Intent is null, do nothing")
            return START_NOT_STICKY
        }
        val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, -1)

        when (intent.action) {
            ACTION_DISMISS_ALARM_FROM_NOTIFICATION -> {
                if (alarmId == -1L) {
                    Timber.e("ACTION_SNOOZE_ALARM_FROM_NOTIFICATION Invalid alarm")
                    return START_NOT_STICKY
                }
                RingingActivity.dismissActivity(this.applicationContext)
                lifecycleScope.launch(Dispatchers.IO) {
                    dismissAlarmUseCase(alarmId)
                }
            }

            ACTION_DISMISS_ALARM -> {
                if (alarmId == -1L) {
                    Timber.e("Invalid alarm ID")
                    stopSelf()
                    return START_NOT_STICKY
                }
                Timber.d("ACTION_DISMISS_ALARM, alarmId: $alarmId")
                notificationHelper.cancelNotification(alarmId.toInt())
                stopAlarmRingtone()
                cleanupAndFinishService()
                return START_NOT_STICKY
            }

            ACTION_SNOOZE_ALARM_FROM_NOTIFICATION -> {
                if (alarmId == -1L) {
                    Timber.e("ACTION_SNOOZE_ALARM_FROM_NOTIFICATION Invalid alarm")
                    return START_NOT_STICKY
                }
                RingingActivity.dismissActivity(this.applicationContext)
                lifecycleScope.launch(Dispatchers.IO) {
                    snoozeAlarmUseCase(alarmId)
                }
            }

            ACTION_SNOOZE_ALARM -> {
                stopAlarmRingtone()
                cleanupAndFinishService()
                return START_NOT_STICKY
            }

            ACTION_ALARM_RINGING -> {
                Timber.d("ACTION_ALARM_RINGING: alarmId $alarmId")
                if (alarmId == -1L) {
                    Timber.e("Invalid alarm ID")
                    stopSelf()
                    return START_NOT_STICKY
                }
                notificationHelper.cancelNotification(alarmId.toInt())
                stopAlarmRingtone()
                lifecycleScope.launch(Dispatchers.IO) {
                    currentAlarm = alarmRepository.getAlarmById(alarmId)
                    if (currentAlarm == null) {
                        Timber.e("Alarm not found for ID $alarmId")
                        stopSelf()
                        return@launch
                    }
                    startRingingAlarm(alarmId)
                    // update notification
                    val is24HourFormat = settingsRepository.getIs24HourFormat()
                    val notificationTitle = "Alarm Ringing"
                    currentAlarm?.let { alarm ->
                        val notificationContent = getAlarmTimeDisplay(
                            hour = alarm.hour,
                            minutes = alarm.minute,
                            is24HourFormat
                        )
                        val notification = notificationHelper.createOngoingAlarmNotification(
                            notificationTitle,
                            notificationContent,
                            alarm.id
                        )
                        Timber.d("ACTION_ALARM_RINGING, show notification id: ${alarm.id}, title: $notificationTitle, content: $notificationContent")
                        startForeground(
                            NotificationHelper.CHANNEL_ALARM_FOREGROUND_NOTIFICATION_ID,
                            notification
                        )
                    }
                }
            }

            else -> {
                Timber.e("Invalid action: ${intent?.action}")
                return START_NOT_STICKY
            }
        }

        return START_STICKY
    }

    private fun startForegroundPlaceHolder() {
        if (isForegroundStarted) return
        isForegroundStarted = true
        val placeholderNotification =
            notificationHelper.createTemporaryRingingNotification(this.applicationContext)
        startForeground(
            NotificationHelper.CHANNEL_ALARM_FOREGROUND_NOTIFICATION_ID,
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
    }

    private fun startRingingAlarm(alarmId: Long) {
        lifecycleScope.launch(Dispatchers.Main) {
            val alarm = alarmRepository.getAlarmById(alarmId)

            if (alarm == null) {
                Timber.e("Alarm not found for ID $alarmId")
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return@launch
            }

            // Play sound
            val currentVolume = systemSettingsManager.getAlarmVolume().toFloat()
            val maxVolume = systemSettingsManager.getMaxAlarmVolume().toFloat()
            val volume = if (maxVolume == 0f) 1f else currentVolume / maxVolume

            mediaPlayerHelper.play(
                alarm.ringtone.uri,
                volume,
                settingsRepository.getVolumeFadeDuration()
            )

            // Wake up screen
            wakeUpScreen(this@AlarmKlaxonService.applicationContext)
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

    companion object {
        const val EXTRA_ALARM_ID = "alarmId"
        const val ACTION_DISMISS_ALARM = "com.jddev.simplealarm.ACTION_DISMISS_ALARM"
        const val ACTION_DISMISS_ALARM_FROM_NOTIFICATION =
            "com.jddev.simplealarm.ACTION_DISMISS_ALARM_FROM_NOTIFICATION"
        const val ACTION_SNOOZE_ALARM = "com.jddev.simplealarm.ACTION_SNOOZE_ALARM"
        const val ACTION_SNOOZE_ALARM_FROM_NOTIFICATION =
            "com.jddev.simplealarm.ACTION_SNOOZE_ALARM_FROM_NOTIFICATION"
        const val ACTION_ALARM_RINGING = "com.jddev.simplealarm.ACTION_ALARM_RINGING"

        fun startRinging(context: Context, alarmId: Long) {
            val intent = Intent(context, AlarmKlaxonService::class.java).apply {
                action = ACTION_ALARM_RINGING
                putExtra(EXTRA_ALARM_ID, alarmId)
            }
            ContextCompat.startForegroundService(context, intent)
        }

        fun dismissAlarm(context: Context, alarmId: Long) {
            val intent = Intent(context, AlarmKlaxonService::class.java).apply {
                action = ACTION_DISMISS_ALARM
                putExtra(EXTRA_ALARM_ID, alarmId)
            }
            ContextCompat.startForegroundService(context, intent)
        }

        fun snoozeAlarm(context: Context, alarmId: Long) {
            val intent = Intent(context, AlarmKlaxonService::class.java).apply {
                action = ACTION_SNOOZE_ALARM
                putExtra(EXTRA_ALARM_ID, alarmId)
            }
            ContextCompat.startForegroundService(context, intent)
        }
    }
}