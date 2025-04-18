package com.jddev.simplealarm.presentation.screens.alarm

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
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
import com.jddev.simplealarm.core.default
import com.jddev.simplealarm.domain.model.alarm.Alarm
import com.jddev.simplealarm.presentation.components.AlarmCard
import com.jddev.simpletouch.ui.foundation.topappbar.stUiLargeTopAppbarScrollBehavior
import com.jddev.simpletouch.ui.utils.StUiPreview
import com.jddev.simpletouch.ui.utils.StUiPreviewWrapper
import java.time.DayOfWeek

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmRoute(
    viewModel: AlarmViewModel = hiltViewModel(),
    scrollBehavior: TopAppBarScrollBehavior,
    onEditAlarm: (Alarm) -> Unit,
) {
    val alarms by viewModel.alarms.collectAsState()
    AlarmScreenContent(
        alarms = alarms, scrollBehavior = scrollBehavior, onUpdate = {
            viewModel.update(it)
        }, onEditAlarm = onEditAlarm
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AlarmScreenContent(
    alarms: List<Alarm>,
    scrollBehavior: TopAppBarScrollBehavior,
    onUpdate: (Alarm) -> Unit,
    onEditAlarm: (Alarm) -> Unit
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
//                modifier = Modifier.animateItem(
//                    fadeInSpec = null, fadeOutSpec = null, placementSpec = tween(
//                        1000
//                    )
//                ),
                modifier = Modifier.animateItem(
                    fadeInSpec = null,
                    fadeOutSpec = null,
                    placementSpec = spring(
                        stiffness = Spring.StiffnessMediumLow,
                        dampingRatio = Spring.DampingRatioNoBouncy
                    )
                ),
                alarm = alarm,
                onToggle = {
                    onUpdate(alarm.copy(enabled = !alarm.enabled))
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
        ), scrollBehavior = stUiLargeTopAppbarScrollBehavior(), {}, {})
    }
}