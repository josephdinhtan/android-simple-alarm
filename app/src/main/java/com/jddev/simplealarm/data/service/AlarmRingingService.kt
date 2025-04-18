package com.jddev.simplealarm.data.service

import android.app.Service.START_NOT_STICKY
import android.content.Context
import android.content.Intent
import androidx.core.app.ServiceCompat.startForeground
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.jddev.simplealarm.core.toStringNotification
import com.jddev.simplealarm.data.helper.MediaPlayerHelper
import com.jddev.simplealarm.data.helper.NotificationHelper
import com.jddev.simplealarm.domain.repository.AlarmRepository
import com.jddev.simplealarm.domain.repository.SettingsRepository
import com.jddev.simplealarm.domain.system.SystemSettingsManager
import dagger.hilt.android.AndroidEntryPoint
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
        when (intent?.action) {
            ACTION_DISMISS_ALARM -> {
                Timber.d("Dismiss action received")
                mediaPlayerHelper.stop()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return START_NOT_STICKY
            }

            else -> {
                val alarmId = intent?.getLongExtra(EXTRA_ALARM_ID, -1) ?: -1
                if (alarmId == -1L) {
                    Timber.e("Invalid alarm ID")
                    stopSelf()
                    return START_NOT_STICKY
                }

                lifecycleScope.launch {
                    val alarm = alarmRepository.getAlarmById(alarmId)

                    if (alarm == null) {
                        Timber.e("Alarm not found for ID $alarmId")
                        stopSelf()
                        return@launch
                    }

                    // Start as a foreground service with notification
                    val is24Hour = settingsRepository.getIs24HourFormat()
                    val notification = notificationHelper.createOngoingAlarmNotification(
                        alarm.toStringNotification(is24Hour)
                    )
                    startForeground(NOTIFICATION_ID_RINGING, notification)

                    // Play sound
                    val currentVolume = systemSettingsManager.getAlarmVolume().toFloat()
                    val maxVolume = systemSettingsManager.getMaxAlarmVolume().toFloat()
                    val volume = if (maxVolume == 0f) 1f else currentVolume / maxVolume

                    mediaPlayerHelper.play(
                        alarm.ringtone.uri,
                        volume,
                        settingsRepository.getVolumeFadeDuration()
                    )
                }

            }
        }

        return START_STICKY
    }

    companion object {
        const val EXTRA_ALARM_ID = "alarmId"
        const val NOTIFICATION_ID_RINGING = 1001
        const val ACTION_DISMISS_ALARM = "com.jddev.simplealarm.ACTION_DISMISS_ALARM"

        fun start(context: Context, alarmId: Long) {
            val intent = Intent(context, AlarmRingingService::class.java).apply {
                putExtra(EXTRA_ALARM_ID, alarmId)
            }
            ContextCompat.startForegroundService(context, intent)
        }
    }
}