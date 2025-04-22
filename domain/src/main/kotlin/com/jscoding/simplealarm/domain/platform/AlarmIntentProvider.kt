package com.jscoding.simplealarm.domain.platform

import android.app.PendingIntent

interface AlarmIntentProvider {
    fun provideAlarmIntent(alarmId: Long, scheduleId: Int): PendingIntent
    fun provideNotificationIntent(
        alarmId: Long,
        hour: Int,
        minute: Int,
        is24HourFormat: Boolean,
        scheduleId: Int,
    ): PendingIntent
}