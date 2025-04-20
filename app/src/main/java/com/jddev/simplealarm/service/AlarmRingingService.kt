package com.jddev.simplealarm.service

import android.app.ActivityOptions
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.jddev.simplealarm.activity.RingingActivity
import com.jddev.simplealarm.helper.NotificationHelper
import com.jscoding.simplealarm.data.helper.MediaPlayerHelper
import com.jscoding.simplealarm.data.utils.toStringNotification
import com.jscoding.simplealarm.domain.platform.SystemSettingsManager
import com.jscoding.simplealarm.domain.repository.AlarmRepository
import com.jscoding.simplealarm.domain.repository.SettingsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
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
    lateinit var notificationHelper: NotificationHelper

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Timber.d("onStartCommand: ${intent?.action}")
        when (intent?.action) {
            ACTION_DISMISS_ALARM -> {
                stopRingingAlarm()
                return START_NOT_STICKY
            }

            ACTION_SNOOZE_ALARM -> {
                stopRingingAlarm()
                return START_NOT_STICKY
            }

            ACTION_ALARM_RINGING -> {
                val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, -1)
                Timber.d("ACTION_ALARM_RINGING: alarmId $alarmId")
                if (alarmId == -1L) {
                    Timber.e("Invalid alarm ID")
                    stopSelf()
                    return START_NOT_STICKY
                } else {
                    startRingingAlarm(alarmId)
                }
            }

            ACTION_ALARM_NOTIFICATION -> {
                val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, -1)
                if (alarmId == -1L) {
                    Timber.e("Invalid alarm ID")
                    stopSelf()
                    return START_NOT_STICKY
                } else {
                    startShowNotification(alarmId)
                }
            }

            else -> {
                Timber.e("Invalid action: ${intent?.action}")
                return START_NOT_STICKY
            }
        }

        return START_STICKY
    }

    private fun stopRingingAlarm() {
        Timber.d("Dismiss action received")
        mediaPlayerHelper.stop()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun startRingingAlarm(alarmId: Long) {
        val placeholderNotification = notificationHelper.createAlertAlarmNotification(
            "", alarmId
        )
        startForeground(
            NotificationHelper.CHANNEL_ALARM_RINGING_NOTIFICATION_ID,
            placeholderNotification
        )
        lifecycleScope.launch(Dispatchers.Main) {
            val alarm = alarmRepository.getAlarmById(alarmId)

            if (alarm == null) {
                Timber.e("Alarm not found for ID $alarmId")
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return@launch
            }

            // Start as a foreground service with notification
            val is24Hour = settingsRepository.getIs24HourFormat()
            val notification = notificationHelper.createOngoingAlarmNotification(
                alarm.toStringNotification(is24Hour),
                alarmId
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(
                NotificationHelper.CHANNEL_ALARM_RINGING_NOTIFICATION_ID,
                notification
            )

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
            wakeUpScreen(this@AlarmRingingService.applicationContext)
//            RingingActivity.startActivity(this@AlarmRingingService.applicationContext, alarmId)

//            val options = ActivityOptions.makeBasic()
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
//                options.setPendingIntentBackgroundActivityStartMode(ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED)
//            }
//            val pendingIntent = PendingIntent.getActivity(
//                this@AlarmRingingService,
//                0,
//                Intent(this@AlarmRingingService, RingingActivity::class.java),
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            )
//
//            pendingIntent.send(
//                this@AlarmRingingService, 0, null, null, null,
//                options.toBundle().toString()
//            )
        }
    }

    private fun startShowNotification(alarmId: Long) {
        val placeholderNotification = notificationHelper.createAlertAlarmNotification(
            "", alarmId
        )
        startForeground(
            NotificationHelper.CHANNEL_ALARM_ALERT_NOTIFICATION_ID,
            placeholderNotification
        )

        lifecycleScope.launch(Dispatchers.Main) {
            val alarm = alarmRepository.getAlarmById(alarmId)
            if (alarm == null) {
                Timber.e("Alarm not found for ID $alarmId")
                stopSelf()
                return@launch
            }

            // Start as a foreground service with notification
            val is24Hour = settingsRepository.getIs24HourFormat()
            val notification = notificationHelper.createAlertAlarmNotification(
                alarm.toStringNotification(is24Hour), alarmId
            )
            Timber.d("Show notification id: $alarmId, content: ${alarm.toStringNotification(is24Hour)}")
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(
                NotificationHelper.CHANNEL_ALARM_ALERT_NOTIFICATION_ID,
                notification
            )
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
        const val ACTION_SNOOZE_ALARM = "com.jddev.simplealarm.ACTION_SNOOZE_ALARM"
        const val ACTION_ALARM_RINGING = "com.jddev.simplealarm.ACTION_ALARM_RINGING"
        const val ACTION_ALARM_NOTIFICATION = "com.jddev.simplealarm.ACTION_ALARM_NOTIFICATION"

        fun startRinging(context: Context, alarmId: Long) {
            val intent = Intent(context, AlarmRingingService::class.java).apply {
                action = ACTION_ALARM_RINGING
                putExtra(EXTRA_ALARM_ID, alarmId)
            }
            ContextCompat.startForegroundService(context, intent)
        }

        fun dismissAlarm(context: Context) {
            val intent = Intent(context, AlarmRingingService::class.java).apply {
                action = ACTION_DISMISS_ALARM
            }
            ContextCompat.startForegroundService(context, intent)
        }

        fun snoozeAlarm(context: Context) {
            val intent = Intent(context, AlarmRingingService::class.java).apply {
                action = ACTION_SNOOZE_ALARM
            }
            ContextCompat.startForegroundService(context, intent)
        }

        fun startNotification(context: Context, alarmId: Long) {
            val intent = Intent(context, AlarmRingingService::class.java).apply {
                action = ACTION_ALARM_NOTIFICATION
                putExtra(EXTRA_ALARM_ID, alarmId)
            }
            ContextCompat.startForegroundService(context, intent)
        }
    }
}