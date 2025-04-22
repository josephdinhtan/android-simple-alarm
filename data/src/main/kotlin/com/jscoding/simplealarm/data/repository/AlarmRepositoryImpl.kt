package com.jscoding.simplealarm.data.repository

import com.jscoding.simplealarm.data.database.alarm.AlarmDao
import com.jscoding.simplealarm.data.mapper.toDomain
import com.jscoding.simplealarm.data.mapper.toEntity
import com.jscoding.simplealarm.domain.model.alarm.Alarm
import com.jscoding.simplealarm.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(
    private val alarmDao: AlarmDao,
) : AlarmRepository {

    override fun getAllAlarms(): Flow<List<Alarm>> {
        return alarmDao.getAllAlarms().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun insertAlarm(alarm: Alarm): Long {
        return alarmDao.insert(alarm.toEntity())
    }

    override suspend fun updateAlarm(alarm: Alarm) {
        alarmDao.update(alarm.toEntity())
    }

    override suspend fun deleteAlarm(alarmId: Long) {
        alarmDao.deleteById(alarmId)
    }

    override suspend fun getAlarmById(id: Long): Alarm? {
        return alarmDao.getById(id)?.toDomain()
    }
}