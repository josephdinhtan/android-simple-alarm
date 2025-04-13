package com.jddev.simplealarm.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {
    @Query("SELECT * FROM alarms ORDER BY hour, minute")
    fun getAllAlarms(): Flow<List<AlarmEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alarm: AlarmEntity): Long

    @Delete
    suspend fun delete(alarm: AlarmEntity)

    @Update
    suspend fun update(alarm: AlarmEntity)

    @Query("SELECT * FROM alarms WHERE id = :id")
    suspend fun getById(id: Int): AlarmEntity?
}