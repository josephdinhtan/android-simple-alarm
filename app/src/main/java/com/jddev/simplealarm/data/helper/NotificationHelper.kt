package com.jddev.simplealarm.data.helper

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import com.jddev.simplealarm.R
import com.jddev.simplealarm.domain.model.alarm.Alarm
import com.jddev.simplealarm.presentation.MainActivity
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    private val context: Context,
) {
    private val channelAlarmAlertNotification = NotificationChannel(
        CHANNEL_ALARM_NOTIFICATION,
        "Alarm alert",
        NotificationManager.IMPORTANCE_HIGH
    ).also {
        it.enableVibration(true)
    }

    init {
        (context.getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager).also {
            it.createNotificationChannel(channelAlarmAlertNotification)
        }
    }

    fun showAlarmAlertNotification(alarm: Alarm) {
        if(isNotificationVisible(CHANNEL_ALARM_ALERT_NOTIFICATION_ID)) {
            Timber.d("Notification already visible")
            return
        }

        val clickIntent = Intent(
            Intent.ACTION_VIEW,
            "https://jddev.com/alarm/".toUri(),
            context,
            MainActivity::class.java
        )
        val clickPendingIntent: PendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(clickIntent)
            getPendingIntent(1, PendingIntent.FLAG_IMMUTABLE)!!
        }
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ALARM_NOTIFICATION)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setSmallIcon(R.drawable.ic_alarm_clock)
            .setContentTitle("Upcoming Alarm")
            .setContentText("$alarm.hour:$alarm.minute - ${alarm.label}")
            .setContentIntent(clickPendingIntent)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        notificationBuilder.addAction(
            0,
            "Dismiss",
            PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    private fun isNotificationVisible(notificationId: Int): Boolean {
        val mNotificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
        val notifications = mNotificationManager!!.activeNotifications
        for (notification in notifications) {
            if (notification.id == notificationId) {
                return true
            }
        }
        return false
    }

    companion object {
        const val CHANNEL_ALARM_NOTIFICATION = "notification_channel_alarm"
        const val CHANNEL_ALARM_ALERT_NOTIFICATION_ID = 0x198
    }
}