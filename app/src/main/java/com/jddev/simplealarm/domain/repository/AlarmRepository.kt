package com.jddev.simplealarm.domain.repository

import com.jddev.simplealarm.domain.model.alarm.Alarm
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    fun getAllAlarms(): Flow<List<Alarm>>
    suspend fun insertAlarm(alarm: Alarm): Long
    suspend fun updateAlarm(alarm: Alarm)
    suspend fun deleteAlarm(alarm: Alarm)
    suspend fun getAlarmById(id: Long): Alarm?
}