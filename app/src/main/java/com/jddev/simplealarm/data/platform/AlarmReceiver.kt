package com.jddev.simplealarm.data.platform

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.jddev.simplealarm.data.helper.ScheduleType
import com.jddev.simplealarm.data.service.AlarmRingingService
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra("alarmId", -1)

        Toast.makeText(context, "Alarm ringing! ID: $alarmId", Toast.LENGTH_LONG).show()

        val style = intent.getStringExtra("type")
        Timber.d("Received intent alarmId: $alarmId, type: $style")
        when (style) {
            ScheduleType.ALARM.style -> {
                AlarmRingingService.startRinging(context, alarmId)
            }

            ScheduleType.NOTIFICATION.style -> {
                AlarmRingingService.startNotification(context, alarmId)
            }
        }
    }
}