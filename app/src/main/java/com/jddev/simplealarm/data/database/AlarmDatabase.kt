package com.jddev.simplealarm.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jddev.simplealarm.data.database.alarm.AlarmDao
import com.jddev.simplealarm.data.database.alarm.AlarmEntity
import com.jddev.simplealarm.data.database.alarm.Converters
import com.jddev.simplealarm.data.database.settings.SettingsDao
import com.jddev.simplealarm.data.database.settings.SettingsEntity

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