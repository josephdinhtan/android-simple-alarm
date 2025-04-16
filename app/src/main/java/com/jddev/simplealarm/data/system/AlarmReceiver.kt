package com.jddev.simplealarm.data.system

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import timber.log.Timber

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra("alarmId", -1)
        // Show full-screen activity or notification here
        Timber.d("Alarm ringing! ID: $alarmId")
        Toast.makeText(context, "Alarm ringing! ID: $alarmId", Toast.LENGTH_LONG).show()

        // fake case show notification here.
    }
}