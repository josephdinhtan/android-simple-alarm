package com.jddev.simplealarm.core
//
//import com.jddev.simplealarm.domain.model.alarm.Alarm
//import com.jddev.simplealarm.domain.model.alarm.Ringtone
//import java.time.ZonedDateTime
//import kotlin.time.Duration.Companion.minutes
//
//fun Alarm.Companion.default(): Alarm {
//    return Alarm(
//        id = 0L,
//        hour = 6,
//        minute = 0,
//        label = "",
//        ringtone = Ringtone.Silent,
//        repeatDays = emptyList(),
//        enabled = true,
//        vibration = true,
//        preAlarmNotificationDuration = 5.minutes,
//        createdAt = System.currentTimeMillis(),
//    )
//}
//
//fun Alarm.toStringTime(is24HourFormat: Boolean): String {
//    return when (is24HourFormat) {
//        true -> "${hour.toString().padStart(2, '0')}:${
//            minute.toString().padStart(2, '0')
//        }"
//
//        false -> {
//            val period = if (hour < 12) "AM" else "PM"
//            val hour12 = when {
//                hour == 0 -> 12
//                hour > 12 -> hour - 12
//                else -> hour
//            }
//            val minutesStr = minute.toString().padStart(2, '0')
//            "$hour12:$minutesStr $period"
//        }
//    }
//}

//fun Alarm.getTimeInMillis(): Long {
//    val now = ZonedDateTime.now()
//
//    // Find next alarm time today or in future repeated days
//    val todayTime = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)
//
//    return if (repeatDays.isEmpty()) {
//        // No repeat — one-time alarm
//        val nextTime = if (todayTime.isAfter(now)) todayTime else todayTime.plusDays(1)
//        nextTime.toInstant().toEpochMilli()
//    } else {
//        // Repeating — find the next matching day
//        var daysToAdd = 0
//        var candidate: ZonedDateTime
//
//        while (daysToAdd <= 7) {
//            candidate = todayTime.plusDays(daysToAdd.toLong())
//            val candidateDay = candidate.dayOfWeek
//            if (candidate.isAfter(now) && repeatDays.contains(candidateDay)) {
//                return candidate.toInstant().toEpochMilli()
//            }
//            daysToAdd++
//        }
//
//        // Fallback
//        todayTime.plusDays(1).toInstant().toEpochMilli()
//    }
//}