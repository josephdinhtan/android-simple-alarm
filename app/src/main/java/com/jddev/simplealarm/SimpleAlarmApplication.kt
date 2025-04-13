package com.jddev.simplealarm

import android.app.Application
import com.jddev.simplealarm.data.AlarmDatabase
import com.jddev.simpletouch.utils.logging.AppTree
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SimpleAlarmApplication : Application() {

    @Inject
    lateinit var appTree: AppTree

    @Inject
    lateinit var appDatabase: AlarmDatabase

    override fun onCreate() {
        super.onCreate()
    }
}