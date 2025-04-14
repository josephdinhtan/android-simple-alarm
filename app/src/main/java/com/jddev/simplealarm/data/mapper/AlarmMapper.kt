package com.jddev.simplealarm.data.mapper

import com.jddev.simplealarm.data.database.alarm.AlarmEntity
import com.jddev.simplealarm.domain.model.Alarm
import java.time.DayOfWeek

fun AlarmEntity.toDomain(): Alarm = Alarm(
    id = id,
    hour = hour,
    minute = minute,
    label = label,
    repeatDays = repeatDaysInt.map { index ->
        DayOfWeek.of(index)
    },
    isEnabled = isEnabled,
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
    isEnabled = isEnabled,
    createdAt = createdAt
)