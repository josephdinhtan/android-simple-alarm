package com.jscoding.simplealarm.data.helper

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import com.jscoding.simplealarm.data.utils.ExactAlarmPermissionHelper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmManagerHelper @Inject constructor(
    private val context: Context,
    private val alarmManager: AlarmManager,
) {
    fun schedule(pendingIntent: PendingIntent, triggerTimeMillis: Long) {
        scheduleExactAlarm(triggerTimeMillis, pendingIntent)
    }

    fun cancel(pendingIntent: PendingIntent) {
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