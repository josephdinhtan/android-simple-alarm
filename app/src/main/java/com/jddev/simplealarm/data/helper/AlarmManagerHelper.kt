package com.jddev.simplealarm.data.helper

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.jddev.simplealarm.data.system.AlarmReceiver
import com.jddev.simplealarm.data.utils.ExactAlarmPermissionHelper
import com.jddev.simplealarm.data.utils.calculateTriggerTime
import com.jddev.simplealarm.domain.model.alarm.Alarm
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

enum class ScheduleType(val style: String) {
    ALARM("alarm"), NOTIFICATION("notification")
}

@Singleton
class AlarmManagerHelper @Inject constructor(
    private val context: Context,
    private val alarmManager: AlarmManager,
) {
    fun schedule(alarm: Alarm, triggerTime: Long, scheduleType: ScheduleType) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("type", scheduleType.style)
            putExtra("alarmId", alarm.id)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        scheduleExactAlarm(triggerTime, pendingIntent)
    }

    fun cancel(alarm: Alarm) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun scheduleExactAlarm(triggerTime: Long, pendingIntent: PendingIntent) {
        try {
            if (ExactAlarmPermissionHelper.canScheduleExactAlarms(context, alarmManager)) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                ExactAlarmPermissionHelper.requestExactAlarmPermission(context)
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}