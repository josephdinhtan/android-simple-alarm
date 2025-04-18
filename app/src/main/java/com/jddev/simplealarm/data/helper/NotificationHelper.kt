package com.jddev.simplealarm.data.helper

import android.Manifest
import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.jddev.simplealarm.R
import com.jddev.simplealarm.data.service.AlarmRingingService
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

    fun showAlarmAlertNotification(contentText: String) {
        Timber.d("Showing notification alarm: $contentText")
        if(isNotificationVisible(CHANNEL_ALARM_ALERT_NOTIFICATION_ID)) {
            Timber.d("Notification already visible")
            return
        }
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ALARM_NOTIFICATION)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setSmallIcon(R.drawable.ic_alarm_clock)
            .setContentTitle("Upcoming Alarm")
            .setContentText(contentText)
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
        notificationBuilder.setColor(ContextCompat.getColor(context, R.color.teal_700))
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Timber.e("Notification permission not granted")
                return@with
            }
            notify(CHANNEL_ALARM_ALERT_NOTIFICATION_ID, notificationBuilder.build())
        }
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

    fun createOngoingAlarmNotification(contentText: String): Notification {
        val dismissIntent = Intent(context, AlarmRingingService::class.java).apply {
            action = AlarmRingingService.ACTION_DISMISS_ALARM
        }
        val dismissPendingIntent = PendingIntent.getService(
            context,
            0,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_ALARM_NOTIFICATION)
            .setContentTitle("Alarm Ringing")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_alarm_clock)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_ALARM) // treated as alarm
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(false)
            .addAction(
                0,
                "Dismiss",
                dismissPendingIntent
            )
            .build()
    }

    companion object {
        const val CHANNEL_ALARM_NOTIFICATION = "notification_channel_alarm"
        const val CHANNEL_ALARM_ALERT_NOTIFICATION_ID = 0x198
    }
}