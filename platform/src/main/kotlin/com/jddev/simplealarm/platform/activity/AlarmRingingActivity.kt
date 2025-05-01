package com.jddev.simplealarm.platform.activity

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.jddev.simplealarm.platform.dto.AlarmDto
import com.jddev.simplealarm.platform.mapper.toDomain
import com.jddev.simplealarm.platform.mapper.toDto
import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.presentation.SingleAlarmRingingApp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber

@AndroidEntryPoint
class AlarmRingingActivity : AppCompatActivity() {

    private val internalBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == DISMISS_ACTION) {
                Timber.d("${intent.action}")
                this@AlarmRingingActivity.finish()
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                internalBroadcastReceiver,
                IntentFilter(DISMISS_ACTION),
                RECEIVER_EXPORTED
            )
        } else {
            registerReceiver(internalBroadcastReceiver, IntentFilter(DISMISS_ACTION))
        }

        val json = intent.getStringExtra(EXTRA_ALARM) ?: run {
            Timber.e("Alarm is invalid, finish")
            finish()
            return
        }
        val alarmDto = Json.decodeFromString<AlarmDto>(json)
        val alarm = alarmDto.toDomain()
        val is24h = intent.getBooleanExtra(EXTRA_IS_24H, true)

        setContent {
            SingleAlarmRingingApp(
                alarm = alarm,
                is24h = is24h,
                onFinished = {
                    this@AlarmRingingActivity.finish()
                }
            )
        }
    }

    override fun onDestroy() {
        unregisterReceiver(internalBroadcastReceiver)
        super.onDestroy()
    }

    @Suppress("DEPRECATION")
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }

        // Optional: dismiss keyguard
        val km = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        km.requestDismissKeyguard(this, null)
    }

    companion object {
        const val EXTRA_ALARM = "alarm_dto"
        const val EXTRA_IS_24H = "is_24h"
        private const val DISMISS_ACTION = "com.jddev.simplealarm.ACTION_FINISH_RINGING_ACTIVITY"

        internal fun startActivity(context: Context, alarm: Alarm, is24h: Boolean) {
            // for testing only, the activity should be started from Notification
            context.startActivity(getStartActivityIntent(context, alarm, is24h))
        }

        internal fun getStartActivityIntent(
            context: Context,
            alarm: Alarm,
            is24h: Boolean,
        ): Intent {
            val jsonAlarmDto = Json.encodeToString(alarm.toDto())
            return Intent(context, AlarmRingingActivity::class.java).apply {
                flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_USER_ACTION
                putExtra(EXTRA_ALARM, jsonAlarmDto)
                putExtra(EXTRA_IS_24H, is24h)
            }
        }

        internal fun dismissActivity(context: Context) {
            val intent = Intent().apply {
                action = DISMISS_ACTION
            }
            Timber.d("try dismiss Ringing Activity")
            context.applicationContext.sendBroadcast(intent)
        }
    }
}