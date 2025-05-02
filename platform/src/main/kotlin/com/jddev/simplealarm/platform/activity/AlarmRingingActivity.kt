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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.jddev.simplealarm.platform.dto.AlarmDto
import com.jddev.simplealarm.platform.mapper.toDomain
import com.jddev.simplealarm.platform.mapper.toDto
import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.presentation.SingleAlarmRingingApp
import com.jscoding.simplealarm.presentation.screens.ringing.AlarmRingingViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber

@AndroidEntryPoint
class AlarmRingingActivity : AppCompatActivity() {

    private val alarmRingingViewmodel: AlarmRingingViewmodel by viewModels()

    private val internalBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                DISMISS_ACTION -> {
                    Timber.d("Dismiss Alarm from UseCase")
                    alarmRingingViewmodel.requestDismissAlarm()
                }

                SNOOZED_ACTION -> {
                    Timber.d("Snooze Alarm from UseCase")
                    alarmRingingViewmodel.requestSnoozeAlarm()
                }

                MISSED_ACTION -> {
                    Timber.d("Missed Alarm from UseCase")
                    alarmRingingViewmodel.requestMissedAlarm()
                }

                else -> {
                    Timber.e("Invalid action: ${intent?.action}")
                }
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val intentFilter = IntentFilter().apply {
            addAction(DISMISS_ACTION)
            addAction(MISSED_ACTION)
            addAction(SNOOZED_ACTION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                internalBroadcastReceiver,
                intentFilter,
                RECEIVER_EXPORTED
            )
        } else {
            registerReceiver(internalBroadcastReceiver, intentFilter)
        }

        val json = intent.getStringExtra(EXTRA_ALARM) ?: run {
            Timber.e("Alarm is invalid, finish")
            finish()
            return
        }
        val alarmDto = Json.decodeFromString<AlarmDto>(json)
        val alarm = alarmDto.toDomain()
        val is24h = intent.getBooleanExtra(EXTRA_IS_24H, true)

        Timber.d("Ringing Activity alarm label: ${alarm.label} id: ${alarm.id}")
        setContent {
            SingleAlarmRingingApp(
                alarmRingingViewmodel = alarmRingingViewmodel,
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
        private const val DISMISS_ACTION = "com.jddev.simplealarm.ACTION_DISMISS_RINGING"
        private const val MISSED_ACTION = "com.jddev.simplealarm.ACTION_MISSED_RINGING"
        private const val SNOOZED_ACTION = "com.jddev.simplealarm.ACTION_SNOOZE_RINGING"

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

        internal fun dismissAlarm(context: Context) {
            val intent = Intent().apply {
                action = DISMISS_ACTION
            }
            Timber.d("try dismiss Ringing Activity")
            context.applicationContext.sendBroadcast(intent)
        }

        internal fun missedAlarm(context: Context) {
            val intent = Intent().apply {
                action = MISSED_ACTION
            }
            Timber.d("send missed to Ringing Activity")
            context.applicationContext.sendBroadcast(intent)
        }

        internal fun snoozeAlarm(context: Context) {
            val intent = Intent().apply {
                action = SNOOZED_ACTION
            }
            context.applicationContext.sendBroadcast(intent)
        }
    }
}