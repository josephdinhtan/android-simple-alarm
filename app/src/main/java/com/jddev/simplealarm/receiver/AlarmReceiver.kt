package com.jddev.simplealarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.jddev.simplealarm.impl.ScheduleType
import com.jddev.simplealarm.service.AlarmKlaxonService
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra("alarmId", -1)

        val style = intent.getStringExtra("type")
        Timber.d("Received intent alarmId: $alarmId, type: $style")
        when (style) {
            ScheduleType.ALARM.value -> {
                Toast.makeText(context, "Alarm firing! ID: $alarmId", Toast.LENGTH_LONG).show()
                AlarmKlaxonService.startRinging(context, alarmId)
            }

            ScheduleType.NOTIFICATION.value -> {
                Toast.makeText(context, "Notification firing! ID: $alarmId", Toast.LENGTH_LONG)
                    .show()
                AlarmKlaxonService.startNotification(context, alarmId)
            }
        }
    }
}