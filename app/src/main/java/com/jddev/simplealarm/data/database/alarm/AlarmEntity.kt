package com.jddev.simplealarm.data.database.alarm

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Duration

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val label: String = "",
    val repeatDaysInt: List<Int>,
    val isEnabled: Boolean = true,
    val preAlarmNotificationMin: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
)