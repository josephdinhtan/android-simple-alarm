package com.jddev.simplealarm.platform.helper

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
import com.jddev.simplealarm.platform.R
import com.jddev.simplealarm.platform.helper.AlarmIntentProvider.Companion.EXTRA_ALARM_ID
import com.jddev.simplealarm.platform.service.AlarmKlaxonService
import com.jscoding.simplealarm.domain.entity.alarm.NotificationAction
import com.jscoding.simplealarm.domain.entity.alarm.NotificationType
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
        it.setSound(null, null)
        it.enableVibration(true)
        it.enableLights(true)
        it.setShowBadge(false)
        it.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        it.description = "Used for ringing alarms"
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

    fun createAlarmNotification(
        title: String,
        contentText: String,
        alarmId: Long,
        type: NotificationType,
        actions: List<NotificationAction>,
    ): Notification {

        val chanelId = when(type) {
            NotificationType.ALARM_SNOOZE -> ALARM_SNOOZE_NOTIFICATION_CHANNEL_ID
            NotificationType.ALARM_UPCOMING -> ALARM_UPCOMING_NOTIFICATION_CHANNEL_ID
            NotificationType.ALARM_MISSED -> ALARM_MISSED_NOTIFICATION_CHANNEL_ID
            NotificationType.ALARM_FIRING -> FIRING_NOTIFICATION_CHANNEL_ID
        }

        val notificationBuilder =
            NotificationCompat.Builder(context, chanelId)
                .setContentTitle(title)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_alarm_clock_2)
                .setOngoing(true)
                .setCategory(NotificationCompat.CATEGORY_ALARM) // treated as alarm
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)

        actions.forEach {
            when (it) {
                NotificationAction.DISMISS -> {
                    notificationBuilder.addAction(
                        0,
                        "Dismiss",
                        getActionPendingIntent(
                            context,
                            alarmId,
                            AlarmKlaxonService.ACTION_DISMISS_ALARM_FROM_NOTIFICATION
                        )
                    )
                }

                NotificationAction.SNOOZE -> {
                    notificationBuilder.addAction(
                        0,
                        "Snooze",
                        getActionPendingIntent(context, alarmId, AlarmKlaxonService.ACTION_SNOOZE_ALARM_FROM_NOTIFICATION)
                    )
                }
            }
        }

        if(type == NotificationType.ALARM_FIRING) {
            val fullScreenIntent = Intent(
                context,
                com.jddev.simplealarm.platform.activity.RingingActivity::class.java
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(EXTRA_ALARM_ID, alarmId)
            }
            val fullScreenPendingIntent = PendingIntent.getActivity(
                context, 0, fullScreenIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            notificationBuilder.setFullScreenIntent(fullScreenPendingIntent, true)
            notificationBuilder.setContentIntent(fullScreenPendingIntent)
        }

        return notificationBuilder.build()
    }

    fun cancelNotification(notificationId: Int) {
        with(NotificationManagerCompat.from(context)) {
            cancel(notificationId)
        }
    }

    fun createOngoingAlarmNotification(
        title: String,
        contentText: String,
        alarmId: Long,
    ): Notification {
        // show ringing activity
        val fullScreenIntent = Intent(
            context,
            com.jddev.simplealarm.platform.activity.RingingActivity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("alarmId", alarmId)
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context, 0, fullScreenIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val dismissPendingIntent =
            getActionPendingIntent(
                context,
                alarmId,
                AlarmKlaxonService.ACTION_DISMISS_ALARM_FROM_NOTIFICATION
            )
        val snoozePendingIntent =
            getActionPendingIntent(
                context,
                alarmId,
                AlarmKlaxonService.ACTION_SNOOZE_ALARM_FROM_NOTIFICATION
            )

        return NotificationCompat.Builder(context, FIRING_NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_alarm_clock_2)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_ALARM) // treated as alarm
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setContentIntent(fullScreenPendingIntent)
            .setAutoCancel(false)
            .addAction(0, "Dismiss", dismissPendingIntent)
            .addAction(0, "Snooze", snoozePendingIntent)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build().apply {
                flags = flags or Notification.FLAG_NO_CLEAR
            }
    }

    fun createTemporaryRingingNotification(context: Context): Notification {
        return NotificationCompat.Builder(context, FIRING_NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Preparing Alarm...")
            .setContentText("Just a moment...")
            .setSmallIcon(R.drawable.ic_alarm_clock_2)
            .setOngoing(true)
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .build().apply {
                flags = flags or Notification.FLAG_NO_CLEAR
            }
    }

    private fun getActionPendingIntent(
        context: Context,
        alarmId: Long,
        actionStr: String,
    ): PendingIntent {
        val intent =
            Intent(context, AlarmKlaxonService::class.java).apply {
                action = actionStr
                putExtra(EXTRA_ALARM_ID, alarmId)
            }

        val pendingIntent = PendingIntent.getService(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return pendingIntent
    }

    companion object {
        const val CHANNEL_ALARM_FOREGROUND_NOTIFICATION_ID = 0x198

        const val ALARM_MISSED_NOTIFICATION_CHANNEL_ID = "alarmMissedNotification"
        const val ALARM_UPCOMING_NOTIFICATION_CHANNEL_ID = "alarmUpcomingNotification"
        const val ALARM_SNOOZE_NOTIFICATION_CHANNEL_ID = "alarmSnoozingNotification"
        const val FIRING_NOTIFICATION_CHANNEL_ID = "firingAlarmsAndTimersNotification"
        const val TIMER_MODEL_NOTIFICATION_CHANNEL_ID = "timerNotification"
        const val STOPWATCH_NOTIFICATION_CHANNEL_ID = "stopwatchNotification"
    }
}