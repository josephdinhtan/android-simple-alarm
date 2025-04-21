package com.jddev.simplealarm

import android.app.Application
import com.jddev.simplealarm.helper.NotificationHelper
import com.jddev.simpletouch.utils.logging.AppTree
import com.jscoding.simplealarm.data.database.AlarmDatabase
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SimpleAlarmApplication : Application() {

    @Inject
    lateinit var appTree: AppTree

    @Inject
    lateinit var appDatabase: AlarmDatabase

    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onCreate() {
        super.onCreate()
        notificationHelper.createNotificationChannels()
    }
}