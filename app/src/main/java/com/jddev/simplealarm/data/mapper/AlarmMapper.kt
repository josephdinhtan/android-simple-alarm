package com.jddev.simplealarm.data.mapper

import android.net.Uri
import com.jddev.simplealarm.data.database.alarm.AlarmEntity
import com.jddev.simplealarm.domain.model.alarm.Alarm
import com.jddev.simplealarm.domain.model.alarm.Ringtone
import java.time.DayOfWeek
import kotlin.time.Duration.Companion.minutes

fun AlarmEntity.toDomain(): Alarm = Alarm(
    id = id,
    hour = hour,
    minute = minute,
    label = label,
    repeatDays = repeatDaysInt.map { index ->
        DayOfWeek.of(index)
    },
    preAlarmNotificationDuration = preAlarmNotificationMin.minutes,
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
    preAlarmNotificationMin = preAlarmNotificationDuration.inWholeMinutes.toInt(),
    isEnabled = enabled,
    vibration = vibration,
    toneUriStr = ringtone.uri.toString(),
    toneTitle = ringtone.title,
    createdAt = createdAt
)