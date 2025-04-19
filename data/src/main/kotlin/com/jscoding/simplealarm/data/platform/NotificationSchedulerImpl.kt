package com.jscoding.simplealarm.data.platform

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.jscoding.simplealarm.data.helper.AlarmManagerHelper
import com.jscoding.simplealarm.data.helper.ScheduleType
import com.jscoding.simplealarm.data.utils.calculateNextTriggerTime
import com.jscoding.simplealarm.data.utils.calculateTriggerTime
import com.jscoding.simplealarm.domain.model.DayOfWeek
import com.jscoding.simplealarm.domain.platform.NotificationScheduler
import javax.inject.Inject

class NotificationSchedulerImpl @Inject constructor(
    private val alarmManagerHelper: AlarmManagerHelper,
    private val context: Context,
) : NotificationScheduler {

    override fun schedule(alarmId: Long, hour: Int, minute: Int) {
        val triggerTime = calculateTriggerTime(hour, minute)
        alarmManagerHelper.schedule(
            alarmId.toScheduleId(),
            alarmId,
            triggerTime,
            ScheduleType.NOTIFICATION
        )
    }

    override fun schedule(alarmId: Long, hour: Int, minute: Int, daysOfWeek: List<DayOfWeek>) {
        daysOfWeek.forEach { dayOfWeek ->
            val triggerTime = calculateNextTriggerTime(dayOfWeek, hour, minute)
            alarmManagerHelper.schedule(
                alarmId.toScheduleId(dayOfWeek),
                alarmId,
                triggerTime,
                ScheduleType.NOTIFICATION
            )
        }
    }

    override fun cancel(alarmId: Long) {
        alarmManagerHelper.cancel(alarmId.toScheduleId())
        for (dayOfWeek in DayOfWeek.entries) {
            alarmManagerHelper.cancel(alarmId.toScheduleId(dayOfWeek))
        }
    }

    override fun isNotificationAllowed(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun Long.toScheduleId(dayOfWeek: DayOfWeek): Int {
        return this.toInt() * 1000 + dayOfWeek.value
    }

    private fun Long.toScheduleId(): Int {
        return this.toInt() * 1000
    }
}