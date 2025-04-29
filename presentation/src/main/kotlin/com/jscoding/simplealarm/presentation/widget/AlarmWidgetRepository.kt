package com.jscoding.simplealarm.presentation.widget

import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.domain.usecase.alarm.GetAlarmByIdUseCase
import com.jscoding.simplealarm.presentation.widget.model.AlarmWidgetModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmWidgetRepository @Inject constructor(
    private val getAlarmByIdUseCase: GetAlarmByIdUseCase
) {
    suspend fun getNextAlarmWidget(alarmId: Long): AlarmWidgetModel? {
        return getAlarmByIdUseCase(alarmId)?.toAlarmWidgetModel()
    }
}