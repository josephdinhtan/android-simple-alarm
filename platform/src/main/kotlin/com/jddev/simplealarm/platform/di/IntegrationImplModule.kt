package com.jddev.simplealarm.platform.di

import com.jddev.simplealarm.platform.impl.AlarmRingingControllerImpl
import com.jddev.simplealarm.platform.impl.NotificationControllerImpl
import com.jscoding.simplealarm.domain.platform.AlarmRingingController
import com.jscoding.simplealarm.domain.platform.AlarmNotificationController
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class IntegrationImplModule {

    @Binds
    abstract fun bindAlarmRingingController(
        impl: AlarmRingingControllerImpl,
    ): AlarmRingingController

    @Binds
    abstract fun bindNotificationController(
        impl: NotificationControllerImpl,
    ): AlarmNotificationController
}