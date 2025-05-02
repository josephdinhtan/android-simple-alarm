package com.jscoding.simplealarm.presentation.screens.alarm

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jddev.simpletouch.ui.foundation.topappbar.stUiLargeTopAppbarScrollBehavior
import com.jddev.simpletouch.ui.utils.StUiPreview
import com.jddev.simpletouch.ui.utils.StUiPreviewWrapper
import com.jscoding.simplealarm.domain.entity.alarm.DayOfWeek
import com.jscoding.simplealarm.domain.entity.alarm.Alarm
import com.jscoding.simplealarm.presentation.components.AlarmCard
import com.jscoding.simplealarm.presentation.utils.default

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmRoute(
    viewModel: AlarmViewModel = hiltViewModel(),
    scrollBehavior: TopAppBarScrollBehavior,
    onEditAlarm: (Alarm) -> Unit,
) {
    val alarms by viewModel.alarms.collectAsState()
    val is24hFormat by viewModel.is24hFormat.collectAsState()
    AlarmScreenContent(
        alarms = alarms, scrollBehavior = scrollBehavior, onEnableUpdate = { alarm, enable ->
            viewModel.onEnableUpdate(alarm, enable)
        }, is24hFormat = is24hFormat, onEditAlarm = onEditAlarm
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmScreenContent(
    alarms: List<Alarm>,
    is24hFormat: Boolean,
    scrollBehavior: TopAppBarScrollBehavior,
    onEnableUpdate: (alarm: Alarm, enable: Boolean) -> Unit,
    onEditAlarm: (Alarm) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(items = alarms, key = { it.id }) { alarm ->
            AlarmCard(
                modifier = Modifier.animateItem(
                    fadeInSpec = null,
                    fadeOutSpec = null,
                    placementSpec = spring(
                        stiffness = Spring.StiffnessMediumLow,
                        dampingRatio = Spring.DampingRatioNoBouncy
                    )
                ),
                alarm = alarm,
                is24hFormat = is24hFormat,
                onToggle = {
                    onEnableUpdate(alarm, it)
                }, onClick = {
                    onEditAlarm(alarm)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@StUiPreview
@Composable
private fun Preview() {
    val repeatDayOfWeeK = listOf(
        DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY
    )
    StUiPreviewWrapper {
        AlarmScreenContent(alarms = listOf(
            Alarm.default().copy(enabled = true, repeatDays = repeatDayOfWeeK),
            Alarm.default().copy(enabled = false),
        ), is24hFormat = false, scrollBehavior = stUiLargeTopAppbarScrollBehavior(), {_, _ ->}, {})
    }
}