package com.jddev.simplealarm.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class CoroutineScopeMain

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class CoroutineScopeIO

@Module
@InstallIn(SingletonComponent::class)
object CoroutineModule {

    @Provides
    @CoroutineScopeMain
    fun provideCoroutineMainScope(): CoroutineScope = kotlinx.coroutines.CoroutineScope(SupervisorJob() +  Dispatchers.Main)

    @Provides
    @CoroutineScopeIO
    fun provideCoroutineIoScope(): CoroutineScope = kotlinx.coroutines.CoroutineScope(SupervisorJob() +  Dispatchers.IO)
}