package com.jscoding.simplealarm.presentation.widget.ui

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.LocalGlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.components.TitleBar
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import com.jscoding.simplealarm.presentation.R
import com.jscoding.simplealarm.presentation.widget.saveSelectedAlarmId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


private val intent = Intent(Intent.ACTION_MAIN).apply {
    component = ComponentName("com.jddev.simplealarm", "com.jddev.simplealarm.MainActivity")
    addCategory(Intent.CATEGORY_LAUNCHER)
    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
}
private val openMainActivityIntent = actionStartActivity(intent)

@Composable
fun AlarmZeroState(
    widgetId: Int,
    glanceId: GlanceId,
    context: Context,
    onAlarmSelected: (alarmId: Int) -> Unit,
) {
    val widgetIdKey = ActionParameters.Key<Int>(AppWidgetManager.EXTRA_APPWIDGET_ID)

    Scaffold(
        titleBar = {
            TitleBar(
                modifier = GlanceModifier.clickable(openMainActivityIntent),
                textColor = GlanceTheme.colors.onSurface,
                startIcon = androidx.glance.ImageProvider(R.drawable.ic_alarm_clock),
                title = "Alarm",
            )
        },
        backgroundColor = GlanceTheme.colors.widgetBackground,
        modifier = GlanceModifier.fillMaxSize(),
    ) {
        Box(modifier = GlanceModifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Button(
                text = "Select Alarm",
                onClick = {
                    onAlarmSelected(1)
                },
            )
        }
    }
}

