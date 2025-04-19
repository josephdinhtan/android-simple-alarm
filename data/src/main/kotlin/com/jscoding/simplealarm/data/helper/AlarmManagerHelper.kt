package com.jscoding.simplealarm.data.helper

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.jscoding.simplealarm.data.platform.AlarmReceiver
import com.jscoding.simplealarm.data.utils.ExactAlarmPermissionHelper
import com.jscoding.simplealarm.domain.model.alarm.Alarm
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
    fun schedule(scheduleId: Int, alarmId: Long, triggerTimeMillis: Long, scheduleType: ScheduleType) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("type", scheduleType.style)
            putExtra("alarmId", alarmId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            scheduleId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        scheduleExactAlarm(triggerTimeMillis, pendingIntent)
    }

    fun cancel(scheduleId: Int) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            scheduleId,
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