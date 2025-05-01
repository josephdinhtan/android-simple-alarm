package com.jscoding.simplealarm.presentation.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.LocalGlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.currentState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.jscoding.simplealarm.domain.usecase.alarm.GetAlarmByIdUseCase
import com.jscoding.simplealarm.presentation.widget.model.AlarmWidgetModel
import com.jscoding.simplealarm.presentation.widget.ui.AlarmWidget
import com.jscoding.simplealarm.presentation.widget.ui.AlarmZeroState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmAppWidget() : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val widgetId = GlanceAppWidgetManager(context).getAppWidgetId(id)
//        val repository = WidgetModelRepository.get(context)
        provideContent {
            GlanceTheme() {
                Content(widgetId)
            }
        }
    }

    @Composable
    private fun Content(widgetId: Int) {

        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current
        val glanceId = LocalGlanceId.current
        val prefs = currentState<Preferences>()
        val alarmId = prefs[AlarmWidgetPreferences.selectedAlarmId]
        var alarmWidgetModel by remember { mutableStateOf<AlarmWidgetModel?>(null) }

        LaunchedEffect(alarmId) {
            if (alarmId == null) return@LaunchedEffect
//            getAlarmByIdUseCase(alarmId.toLong())?.let { alarm ->
//                alarmWidgetModel = alarm.toAlarmWidgetModel()
//            }
        }

        if (alarmId == null || alarmWidgetModel == null) {
            AlarmZeroState(widgetId, glanceId, context) {
                coroutineScope.launch(Dispatchers.IO) {
                    saveSelectedAlarmId(
                        context,
                        glanceId,
                        alarmId = 1 // Hard code for testing
                    )
                }
            }
        } else {
            alarmWidgetModel?.let {
                AlarmWidget(
                    alarm = it,
                    is24hFormat = true,
                    onAlarmChange = {},
                    onClick = {}
                )
            }
        }
    }
}

object AlarmWidgetPreferences {
    val selectedAlarmId = intPreferencesKey("selected_alarm_id")
}

suspend fun saveSelectedAlarmId(
    context: Context,
    glanceId: GlanceId,
    alarmId: Int,
) {
    updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
        prefs.toMutablePreferences().apply {
            this[AlarmWidgetPreferences.selectedAlarmId] = alarmId
        }
    }
}