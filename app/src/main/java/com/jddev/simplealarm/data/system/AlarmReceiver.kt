package com.jddev.simplealarm.data.system

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.jddev.simplealarm.data.di.CoroutineScopeMain
import com.jddev.simplealarm.data.helper.MediaPlayerHelper
import com.jddev.simplealarm.data.helper.NotificationHelper
import com.jddev.simplealarm.domain.repository.AlarmRepository
import com.jddev.simplealarm.domain.repository.SettingsRepository
import com.jddev.simplealarm.domain.system.SystemSettingsManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationHelper: NotificationHelper
    @Inject
    lateinit var alarmRepository: AlarmRepository
    @Inject
    lateinit var mediaPlayerHelper: MediaPlayerHelper
    @Inject
    lateinit var settingsRepository: SettingsRepository
    @Inject
    lateinit var systemSettingsManager: SystemSettingsManager

    @Inject
    @CoroutineScopeMain
    lateinit var coroutineScopeMain: CoroutineScope

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra("alarmId", -1)

        Timber.d("Alarm ringing! ID: $alarmId")
        Toast.makeText(context, "Alarm ringing! ID: $alarmId", Toast.LENGTH_LONG).show()

        val style = intent.getStringExtra("type")
        coroutineScopeMain.launch {
            alarmRepository.getAlarmById(alarmId)?.let { alarm ->
                val currentVolume = systemSettingsManager.getAlarmVolume().toFloat()
                val maxVolume = systemSettingsManager.getMaxAlarmVolume().toFloat()
                when (style) {
                    "alarm" -> {
                        notificationHelper.showAlarmAlertNotification(alarm)
                        alarm.tone.let {
                            mediaPlayerHelper.play(
                                it.uri,
                                currentVolume / maxVolume,
                                settingsRepository.getVolumeFadeDuration()
                            )
                        }
                    }

                    "notification" -> notificationHelper.showAlarmAlertNotification(alarm)
                    else -> {
                        Timber.e("Unknown type: $style")
                    }
                }
            } ?: run {
                Timber.e("Alarm not found id: $alarmId")
            }
        }
    }
}