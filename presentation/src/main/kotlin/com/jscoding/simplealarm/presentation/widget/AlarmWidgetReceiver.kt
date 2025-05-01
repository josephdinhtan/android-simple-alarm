package com.jscoding.simplealarm.presentation.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.Bundle
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmWidgetReceiver : GlanceAppWidgetReceiver() {

//    @Inject
//    lateinit var getAlarmByIdUseCase: GetAlarmByIdUseCase

    override val glanceAppWidget: GlanceAppWidget = AlarmAppWidget()

//    @Inject
//    lateinit var repository: WidgetModelRepository

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
//        repository.cleanupWidgetModels(context)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle,
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
    }
}