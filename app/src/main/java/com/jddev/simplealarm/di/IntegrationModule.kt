package com.jddev.simplealarm.di

import com.jddev.simplealarm.integration.AlarmIntentProviderImpl
import com.jscoding.simplealarm.domain.platform.AlarmIntentProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class IntegrationModule {

    @Binds
    abstract fun bindAlarmIntentProvider(
        alarmIntentProviderImpl: AlarmIntentProviderImpl
    ): AlarmIntentProvider
}