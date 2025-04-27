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
import com.jddev.simplealarm.platform.helper.AlarmIntentProvider
import com.jddev.simplealarm.platform.helper.AlarmIntentProvider.Companion.EXTRA_ALARM_ID
import com.jscoding.simplealarm.presentation.SingleAlarmRingingApp
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class RingingActivity : AppCompatActivity() {

    private val internalBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == DISMISS_ACTION) {
                Timber.d("${intent.action}")
                this@RingingActivity.finish()
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

        val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, -1)
        if (alarmId == -1L) {
            Timber.e("Alarm id not found")
            finish()
            return
        }

        setContent {
            SingleAlarmRingingApp(
                alarmId = alarmId,
                onFinished = {
                    this@RingingActivity.finish()
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

        private const val DISMISS_ACTION = "com.jddev.simplealarm.ACTION_FINISH_RINGING_ACTIVITY"

        fun startActivity(context: Context, alarmId: Long) {
            val intent = Intent(context, RingingActivity::class.java).apply {
                flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_USER_ACTION
                putExtra(AlarmIntentProvider.EXTRA_ALARM_ID, alarmId)
            }
            context.startActivity(intent)
        }

        fun dismissActivity(context: Context) {
            val intent = Intent().apply {
                action = DISMISS_ACTION
            }
            Timber.d("dismissActivity")
            context.applicationContext.sendBroadcast(intent)
        }
    }
}