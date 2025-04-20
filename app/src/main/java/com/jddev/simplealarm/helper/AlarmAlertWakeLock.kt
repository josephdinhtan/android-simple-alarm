package com.jddev.simplealarm.helper

import android.content.Context
import android.os.PowerManager

object AlarmAlertWakeLock {
    private const val TAG = "com.jscoding.simplealarm::AlarmAlertWakeLock"

    private var sCpuWakeLock: PowerManager.WakeLock? = null

    @JvmStatic
    fun createPartialWakeLock(context: Context): PowerManager.WakeLock {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG)
    }

    @JvmStatic
    fun acquireCpuWakeLock(context: Context) {
        if (sCpuWakeLock != null) {
            return
        }

        sCpuWakeLock = createPartialWakeLock(context)
        sCpuWakeLock!!.acquire(10*60*1000L /*10 minutes*/)
    }

    @JvmStatic
    fun acquireScreenCpuWakeLock(context: Context) {
        if (sCpuWakeLock != null) {
            return
        }
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        sCpuWakeLock = pm.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK
                    or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE, TAG)
        sCpuWakeLock!!.acquire(10*60*1000L /*10 minutes*/)
    }

    @JvmStatic
    fun releaseCpuLock() {
        if (sCpuWakeLock != null) {
            sCpuWakeLock!!.release()
            sCpuWakeLock = null
        }
    }
}