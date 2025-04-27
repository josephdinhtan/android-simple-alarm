package com.jscoding.simplealarm.domain.repository

import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    fun getAllAlarms(): Flow<List<Alarm>>
    suspend fun insertAlarm(alarm: Alarm): Long
    suspend fun updateAlarm(alarm: Alarm)
    suspend fun deleteAlarm(alarm: Alarm)
    suspend fun getAlarmById(id: Long): Alarm?
}