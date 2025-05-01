package com.jddev.simplealarm.platform.mapper

import android.net.Uri
import com.jddev.simplealarm.platform.dto.AlarmDto
import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.entity.alarm.DayOfWeek
import com.jscoding.simplealarm.domain.entity.alarm.Ringtone
import kotlin.time.Duration.Companion.milliseconds

internal fun Alarm.toDto(): AlarmDto = AlarmDto(
    id = id,
    hour = hour,
    minute = minute,
    label = label,
    ringtoneTitle = ringtone.title,
    ringtoneUri = ringtone.uri.toString(),
    vibration = vibration,
    repeatDays = repeatDays.map { it.ordinal },
    snoozeTimeMillis = snoozeTime.inWholeMilliseconds,
    preAlarmNotificationMillis = preAlarmNotificationDuration.inWholeMilliseconds
)

internal fun AlarmDto.toDomain(): Alarm = Alarm(
    id = id,
    hour = hour,
    minute = minute,
    label = label,
    ringtone = Ringtone(uri = Uri.parse(ringtoneUri), title = ringtoneTitle), // or your own converter
    vibration = vibration,
    repeatDays = repeatDays.map { DayOfWeek.entries[it] },
    enabled = true, // or derive from context
    snoozeTime = snoozeTimeMillis.milliseconds,
    preAlarmNotificationDuration = preAlarmNotificationMillis.milliseconds,
    createdAt = System.currentTimeMillis() // or another source
)