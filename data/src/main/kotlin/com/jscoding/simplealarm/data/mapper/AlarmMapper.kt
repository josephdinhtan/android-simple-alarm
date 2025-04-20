package com.jscoding.simplealarm.data.mapper

import android.net.Uri
import com.jscoding.simplealarm.data.database.alarm.AlarmEntity
import com.jscoding.simplealarm.data.utils.of
import com.jscoding.simplealarm.domain.model.DayOfWeek
import com.jscoding.simplealarm.domain.model.alarm.Alarm
import com.jscoding.simplealarm.domain.model.alarm.Ringtone
import kotlin.time.Duration.Companion.seconds

fun AlarmEntity.toDomain(): Alarm = Alarm(
    id = id,
    hour = hour,
    minute = minute,
    label = label,
    repeatDays = repeatDaysInt.map {
        DayOfWeek.of(it)
    },
    snoozeTime = snoozeTimeSeconds.seconds,
    preAlarmNotificationDuration = preAlarmNotificationSeconds.seconds,
    enabled = isEnabled,
    vibration = vibration,
    ringtone = Ringtone(
        uri = Uri.parse(toneUriStr), title = toneTitle
    ),
    createdAt = createdAt
)

fun Alarm.toEntity(): AlarmEntity = AlarmEntity(
    id = id,
    hour = hour,
    minute = minute,
    label = label,
    repeatDaysInt = repeatDays.map {
        it.value
    },
    snoozeTimeSeconds = snoozeTime.inWholeSeconds.toInt(),
    preAlarmNotificationSeconds = preAlarmNotificationDuration.inWholeSeconds.toInt(),
    isEnabled = enabled,
    vibration = vibration,
    toneUriStr = ringtone.uri.toString(),
    toneTitle = ringtone.title,
    createdAt = createdAt
)