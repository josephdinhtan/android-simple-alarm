package com.jddev.simplealarm.data.di

import android.content.Context
import androidx.room.Room
import com.jddev.simplealarm.data.AlarmDao
import com.jddev.simplealarm.data.AlarmDatabase
import com.jddev.simplealarm.data.repository.AlarmRepositoryImpl
import com.jddev.simplealarm.domain.repository.AlarmRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DataModules {
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AlarmDatabase = Room.databaseBuilder(
        context,
        AlarmDatabase::class.java,
        "alarm_db"
    ).build()

    @Provides
    fun provideAlarmDao(db: AlarmDatabase): AlarmDao = db.alarmDao()
}