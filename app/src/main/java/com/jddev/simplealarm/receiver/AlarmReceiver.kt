package com.jddev.simplealarm.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.jddev.simplealarm.helper.NotificationHelper
import com.jddev.simplealarm.impl.ScheduleType
import com.jddev.simplealarm.service.AlarmKlaxonService
import com.jscoding.simplealarm.data.utils.getAlarmTimeDisplay
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationHelper: NotificationHelper
    @Inject
    lateinit var notificationManager: NotificationManager

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra("alarmId", -1)

        val style = intent.getStringExtra("type")
        Timber.d("Received intent alarmId: $alarmId, type: $style")
        when (style) {
            ScheduleType.ALARM.value -> {
                AlarmKlaxonService.startRinging(context, alarmId)
            }

            ScheduleType.NOTIFICATION.value -> {
                val hour = intent.getIntExtra("hour", 0)
                val minute = intent.getIntExtra("minute", 0)
                val is24HourFormat = intent.getBooleanExtra("is24HourFormat", true)
                Timber.d("Show notification id: $alarmId, hour: $hour, minute: $minute, is24HourFormat: $is24HourFormat")
                showPreAlarmNotification(alarmId, hour, minute, is24HourFormat)
            }
        }
    }

    private fun showPreAlarmNotification(
        alarmId: Long,
        hour: Int,
        minute: Int,
        is24HourFormat: Boolean,
    ) {
        val notificationTitle = "Upcoming alarm"
        val notificationContent = getAlarmTimeDisplay(
            hour = hour,
            minutes = minute,
            is24HourFormat
        )
        Timber.d("Show notification id: ${alarmId}, title: $notificationTitle, content: $notificationContent")
        val notification = notificationHelper.createAlarmDismissNotification(
            notificationTitle,
            notificationContent,
            alarmId
        )
        notificationManager.notify(
            alarmId.toInt(),
            notification
        )
    }
}