package com.jscoding.simplealarm.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.jscoding.simplealarm.data.database.AlarmDatabase
import com.jscoding.simplealarm.data.database.alarm.AlarmDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModules {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AlarmDatabase = Room.databaseBuilder(
        context.applicationContext,
        AlarmDatabase::class.java,
        "alarm_db"
    ).build()

    @Provides
    @Singleton
    fun provideAlarmDao(db: AlarmDatabase): AlarmDao = db.alarmDao()

    @Provides
    @Singleton
    fun provideDataStorePreferences(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = PreferenceDataStoreFactory.create(
        scope = CoroutineScope(context = Dispatchers.IO + SupervisorJob()),
        produceFile = {
            context.preferencesDataStoreFile(name = "user_settings_preferences")
        }
    )
}