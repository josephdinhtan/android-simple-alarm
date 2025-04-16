package com.jddev.simplealarm.data.repository

import com.jddev.simplealarm.data.database.alarm.AlarmDao
import com.jddev.simplealarm.data.mapper.toDomain
import com.jddev.simplealarm.data.mapper.toEntity
import com.jddev.simplealarm.domain.model.alarm.Alarm
import com.jddev.simplealarm.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor (
    private val alarmDao: AlarmDao
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

    override suspend fun deleteAlarm(alarm: Alarm) {
        alarmDao.delete(alarm.toEntity())
    }

    override suspend fun getAlarmById(id: Long): Alarm? {
        return alarmDao.getById(id)?.toDomain()
    }
}