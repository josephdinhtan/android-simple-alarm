package com.jddev.simplealarm.helper

import android.Manifest
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
import com.jddev.simplealarm.activity.RingingActivity
import com.jddev.simplealarm.service.AlarmKlaxonService
import com.jscoding.simplealarm.data.R
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    private val context: Context,
    private val notificationManager: NotificationManager,
) {
    private val channelFiringAlarmNotification = NotificationChannel(
        FIRING_NOTIFICATION_CHANNEL_ID,
        "Firing alarms & timers",
        NotificationManager.IMPORTANCE_HIGH
    ).also {
        it.enableVibration(true)
        it.enableLights(true)
        it.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
    }
    private val channelSnoozedAlarmNotification = NotificationChannel(
        ALARM_SNOOZE_NOTIFICATION_CHANNEL_ID,
        "Snoozed alarms",
        NotificationManager.IMPORTANCE_HIGH
    ).also {
        it.enableLights(true)
        it.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
    }
    private val channelUpcomingAlarmNotification = NotificationChannel(
        ALARM_UPCOMING_NOTIFICATION_CHANNEL_ID,
        "Upcoming alarms",
        NotificationManager.IMPORTANCE_HIGH
    ).also {
        it.enableVibration(true)
        it.enableLights(true)
        it.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
    }

    fun createNotificationChannels() {
        notificationManager.createNotificationChannel(channelFiringAlarmNotification)
        notificationManager.createNotificationChannel(channelSnoozedAlarmNotification)
        notificationManager.createNotificationChannel(channelUpcomingAlarmNotification)
    }

    fun showNotification(notificationChannelId: Int, notification: Notification) {
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Timber.e("Notification permission not granted")
                return@with
            }
            notify(notificationChannelId, notification)
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

    fun createAlarmDismissNotification(
        title: String,
        contentText: String,
        alarmId: Long,
    ): Notification {
        val dismissIntent = Intent(context, AlarmKlaxonService::class.java).apply {
            action = AlarmKlaxonService.ACTION_DISMISS_ALARM
            putExtra(AlarmKlaxonService.EXTRA_ALARM_ID, alarmId)
        }

        val dismissPendingIntent = PendingIntent.getService(
            context,
            0,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, ALARM_SNOOZE_NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
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

    fun createOngoingAlarmNotification(
        title: String,
        contentText: String,
        alarmId: Long,
    ): Notification {
        // show ringing activity
        val fullScreenIntent = Intent(context, RingingActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("alarmId", alarmId)
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context, 0, fullScreenIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val dismissPendingIntent =
            getActionPendingIntent(alarmId, context, AlarmKlaxonService.ACTION_DISMISS_ALARM)
        val snoozePendingIntent =
            getActionPendingIntent(alarmId, context, AlarmKlaxonService.ACTION_SNOOZE_ALARM)

        return NotificationCompat.Builder(context, FIRING_NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_alarm_clock)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_ALARM) // treated as alarm
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setContentIntent(fullScreenPendingIntent)
            .setAutoCancel(false)
            .addAction(
                0,
                "Dismiss",
                dismissPendingIntent
            )
            .addAction(
                0,
                "Snooze",
                snoozePendingIntent
            )
            .build()
    }

    fun createTemporaryRingingNotification(context: Context): Notification {
        return NotificationCompat.Builder(context, FIRING_NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Preparing Alarm...")
            .setContentText("Just a moment...")
            .setSmallIcon(R.drawable.ic_alarm_clock)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun getActionPendingIntent(
        alarmId: Long,
        context: Context,
        actionStr: String,
    ): PendingIntent {
        val intent =
            Intent(context, AlarmKlaxonService::class.java).apply {
                action = actionStr
                putExtra(AlarmKlaxonService.EXTRA_ALARM_ID, alarmId)
            }

        val pendingIntent = PendingIntent.getService(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return pendingIntent
    }

    fun cancelNotification(notificationId: Int) {
        with(NotificationManagerCompat.from(context)) {
            cancel(notificationId)
        }
    }

    companion object {
        const val CHANNEL_ALARM_FOREGROUND_NOTIFICATION_ID = 0x198
//        const val CHANNEL_ALARM_SNOOZE_NOTIFICATION_ID = 0x199

        const val ALARM_MISSED_NOTIFICATION_CHANNEL_ID = "alarmMissedNotification"
        const val ALARM_UPCOMING_NOTIFICATION_CHANNEL_ID = "alarmUpcomingNotification"
        const val ALARM_SNOOZE_NOTIFICATION_CHANNEL_ID = "alarmSnoozingNotification"
        const val FIRING_NOTIFICATION_CHANNEL_ID = "firingAlarmsAndTimersNotification"
        const val TIMER_MODEL_NOTIFICATION_CHANNEL_ID = "timerNotification"
        const val STOPWATCH_NOTIFICATION_CHANNEL_ID = "stopwatchNotification"
    }
}