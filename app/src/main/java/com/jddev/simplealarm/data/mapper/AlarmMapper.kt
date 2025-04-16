package com.jddev.simplealarm.data.mapper

import android.net.Uri
import com.jddev.simplealarm.data.database.alarm.AlarmEntity
import com.jddev.simplealarm.domain.model.alarm.Alarm
import com.jddev.simplealarm.domain.model.alarm.AlarmTone
import java.time.DayOfWeek
import java.time.Duration

fun AlarmEntity.toDomain(): Alarm = Alarm(
    id = id,
    hour = hour,
    minute = minute,
    label = label,
    repeatDays = repeatDaysInt.map { index ->
        DayOfWeek.of(index)
    },
    preAlarmNotificationDuration = Duration.ofMinutes(preAlarmNotificationMin.toLong()),
    isEnabled = isEnabled,
    tone = AlarmTone(
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
    preAlarmNotificationMin = preAlarmNotificationDuration.toMinutes().toInt(),
    isEnabled = isEnabled,
    toneUriStr = tone.uri.toString(),
    toneTitle = tone.title,
    createdAt = createdAt
)