package com.jddev.simplealarm.di

import com.jddev.simplealarm.impl.AlarmIntentProviderImpl
import com.jddev.simplealarm.impl.AlarmRingingControllerImpl
import com.jddev.simplealarm.impl.NotificationControllerImpl
import com.jscoding.simplealarm.domain.platform.AlarmIntentProvider
import com.jscoding.simplealarm.domain.platform.AlarmRingingController
import com.jscoding.simplealarm.domain.platform.NotificationController
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class IntegrationImplModule {

    @Binds
    abstract fun bindAlarmIntentProvider(
        impl: AlarmIntentProviderImpl,
    ): AlarmIntentProvider

    @Binds
    abstract fun bindAlarmRingingController(
        impl: AlarmRingingControllerImpl,
    ): AlarmRingingController

    @Binds
    abstract fun bindNotificationController(
        impl: NotificationControllerImpl,
    ): NotificationController
}