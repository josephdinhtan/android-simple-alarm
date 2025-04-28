package com.jscoding.simplealarm.data.repository

import com.jscoding.simplealarm.data.local.alarm.AlarmDao
import com.jscoding.simplealarm.data.mapper.toDomain
import com.jscoding.simplealarm.data.mapper.toEntity
import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(
    private val alarmDao: AlarmDao,
) : AlarmRepository {

    override fun getAllAlarms(): Flow<List<Alarm>> =
        alarmDao.getAllAlarms().map { list -> list.map { it.toDomain() } }

    override suspend fun insertAlarm(alarm: Alarm) =
        try {
            val insertedId = alarmDao.insert(alarm.toEntity())
            if (insertedId > 0) {
                Result.success(insertedId)
            } else {
                Result.failure(Exception("Failed to insert alarm"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun updateAlarm(alarm: Alarm) =
        try {
            val updatedRows = alarmDao.update(alarm.toEntity())
            if (updatedRows > 0) {
                Result.success(updatedRows)
            } else {
                Result.failure(Exception("Failed to update alarm"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun deleteAlarm(alarm: Alarm) =
        try {
            val deletedRows = alarmDao.delete(alarm.toEntity())
            if (deletedRows > 0) {
                Result.success(deletedRows)
            } else {
                Result.failure(Exception("Failed to delete alarm"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun getAlarmById(id: Long) =
        try {
            val alarmEntity = alarmDao.getById(id)
            if (alarmEntity != null) {
                Result.success(alarmEntity.toDomain())
            } else {
                Result.failure(Exception("Alarm not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
}