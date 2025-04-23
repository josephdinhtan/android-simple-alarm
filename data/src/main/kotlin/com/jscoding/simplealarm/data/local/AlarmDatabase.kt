package com.jscoding.simplealarm.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jscoding.simplealarm.data.local.alarm.AlarmDao
import com.jscoding.simplealarm.data.local.alarm.AlarmEntity
import com.jscoding.simplealarm.data.local.alarm.Converters

//@Database(entities = [AlarmEntity::class, SettingsEntity::class], version = 1)
//@TypeConverters(Converters::class)
//abstract class AlarmDatabase : RoomDatabase() {
//    abstract fun alarmDao(): AlarmDao
//    abstract fun settingsDao(): SettingsDao
//}

@Database(entities = [AlarmEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AlarmDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao
}