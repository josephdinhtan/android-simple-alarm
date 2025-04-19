package com.jddev.simplealarm.data.di

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import com.jddev.simplealarm.data.platform.AlarmSchedulerImpl
import com.jddev.simplealarm.data.platform.MediaPlayerImpl
import com.jddev.simplealarm.data.platform.NotificationSchedulerImpl
import com.jddev.simplealarm.data.platform.SystemSettingsManagerImpl
import com.jddev.simplealarm.domain.platform.AlarmScheduler
import com.jddev.simplealarm.domain.platform.NotificationScheduler
import com.jddev.simplealarm.domain.platform.SystemSettingsManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AndroidSystemModule {

    @Provides
    fun provideContext(@ApplicationContext context: Context): Context = context.applicationContext

    @Provides
    @Singleton
    fun provideAlarmManager(@ApplicationContext context: Context): AlarmManager {
        return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    @Provides
    @Singleton
    fun provideAudioManager(@ApplicationContext context: Context): AudioManager {
        return context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @Provides
    @Singleton
    fun provideRingToneManager(@ApplicationContext context: Context): RingtoneManager {
        return RingtoneManager(context)
    }

    @Provides
    fun provideMediaPlayer(): MediaPlayer {
        return MediaPlayer()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSystemModule {
    @Singleton
    @Binds
    abstract fun bindsAlarmScheduler(
        impl: AlarmSchedulerImpl
    ): AlarmScheduler

    @Singleton
    @Binds
    abstract fun bindsSystemSettingsManager(
        impl: SystemSettingsManagerImpl
    ): SystemSettingsManager

    @Singleton
    @Binds
    abstract fun bindsNotificationController(
        impl: NotificationSchedulerImpl
    ): NotificationScheduler

    @Singleton
    @Binds
    abstract fun bindsMediaPlayer(
        impl: MediaPlayerImpl
    ): com.jddev.simplealarm.domain.platform.MediaPlayer
}