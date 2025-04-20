package com.jscoding.simplealarm.data.database.alarm

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val label: String,
    val repeatDaysInt: List<Int>,
    val isEnabled: Boolean,
    val vibration: Boolean,
    val snoozeTimeSeconds: Int,
    val preAlarmNotificationSeconds: Int,
    val toneUriStr: String,
    val toneTitle: String,
    val createdAt: Long = System.currentTimeMillis(),
)

