package com.jddev.simplealarm.data.di

import com.jddev.simplealarm.data.repository.AlarmRepositoryImpl
import com.jddev.simplealarm.data.repository.SettingsRepositoryImpl
import com.jddev.simplealarm.domain.repository.AlarmRepository
import com.jddev.simplealarm.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun provideAlarmRepository(
        impl: AlarmRepositoryImpl
    ): AlarmRepository

    @Singleton
    @Binds
    abstract fun provideSettingsRepository(
        impl: SettingsRepositoryImpl
    ): SettingsRepository
}