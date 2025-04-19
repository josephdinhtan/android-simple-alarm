package com.jscoding.simplealarm.data.di

import com.jscoding.simplealarm.data.repository.AlarmRepositoryImpl
import com.jscoding.simplealarm.data.repository.SettingsRepositoryImpl
import com.jscoding.simplealarm.domain.repository.AlarmRepository
import com.jscoding.simplealarm.domain.repository.SettingsRepository
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
    abstract fun bindsAlarmRepository(
        impl: AlarmRepositoryImpl,
    ): AlarmRepository

    @Singleton
    @Binds
    abstract fun bindsSettingsRepository(
        impl: SettingsRepositoryImpl,
    ): SettingsRepository
}