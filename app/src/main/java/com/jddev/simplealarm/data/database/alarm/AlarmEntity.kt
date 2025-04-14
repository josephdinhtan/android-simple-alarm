package com.jddev.simplealarm.data.database.alarm

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val label: String = "",
    val repeatDaysInt: List<Int>, // 0=Sunday, ..., 6=Saturday
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)