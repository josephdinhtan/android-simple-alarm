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
import com.jddev.simplealarm.platform.activity.AlarmRingingActivity
import com.jddev.simplealarm.platform.mapper.toDto
import com.jddev.simplealarm.platform.receiver.AlarmActionReceiver
import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.entity.alarm.NotificationAction
import com.jscoding.simplealarm.domain.entity.alarm.NotificationType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
        it.enableVibration(false)
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
        alarm: Alarm,
        is24h: Boolean,
        type: NotificationType,
        actions: List<NotificationAction>,
    ): Notification {

        val chanelId = when (type) {
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
                        getAlarmActionPendingIntent(
                            context,
                            alarm,
                            AlarmActionReceiver.ACTION_DISMISS_ALARM_FROM_NOTIFICATION
                        )
                    )
                }

                NotificationAction.SNOOZE -> {
                    notificationBuilder.addAction(
                        0,
                        "Snooze",
                        getAlarmActionPendingIntent(
                            context,
                            alarm,
                            AlarmActionReceiver.ACTION_SNOOZE_ALARM_FROM_NOTIFICATION
                        )
                    )
                }
            }
        }

        if (type == NotificationType.ALARM_FIRING) {
            val fullScreenIntent = AlarmRingingActivity.getStartActivityIntent(context, alarm, is24h)
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

    private fun getAlarmActionPendingIntent(
        context: Context,
        alarm: Alarm,
        actionStr: String,
    ): PendingIntent {
        val jsonAlarmDto = Json.encodeToString(alarm.toDto())
        val intent =
            Intent(context, AlarmActionReceiver::class.java).apply {
                action = actionStr
                putExtra(AlarmActionReceiver.EXTRA_ALARM, jsonAlarmDto)
            }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
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