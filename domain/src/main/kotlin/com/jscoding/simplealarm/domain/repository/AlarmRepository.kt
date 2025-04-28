package com.jscoding.simplealarm.domain.repository

import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    fun getAllAlarms(): Flow<List<Alarm>>
    suspend fun insertAlarm(alarm: Alarm): Result<Long>
    suspend fun updateAlarm(alarm: Alarm): Result<Int>
    suspend fun deleteAlarm(alarm: Alarm): Result<Int>
    suspend fun getAlarmById(id: Long): Result<Alarm>
}