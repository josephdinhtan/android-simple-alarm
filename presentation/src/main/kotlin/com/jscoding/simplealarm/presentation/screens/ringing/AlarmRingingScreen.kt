package com.jscoding.simplealarm.presentation.screens.ringing

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.jscoding.simplealarm.domain.entity.alarm.Alarm

@Composable
fun AlarmRingingScreen(
    viewModel: AlarmRingingViewmodel = hiltViewModel(),
    alarm: Alarm,
    is24h: Boolean,
    onFinished: () -> Unit,
) {
    LaunchedEffect(alarm) {
        viewModel.setupAlarm(alarm, is24h)
    }
    val alarmRingingState = viewModel.alarmRingingState.collectAsState().value
    val shouldFinish = viewModel.shouldFinish.collectAsState().value
    LaunchedEffect(shouldFinish) {
        if (shouldFinish) {
            onFinished()
        }
    }
    AniVisibility(visible = alarmRingingState is AlarmRingingState.Ringing) {
        if (alarmRingingState is AlarmRingingState.Ringing) {
            RingingScreen(
                time = alarmRingingState.timeDisplay,
                label = alarmRingingState.label,
                onSnooze = { viewModel.snoozeAlarm() },
                onDismiss = { viewModel.dismissAlarm() }
            )
        }
    }

    AniVisibility(visible = alarmRingingState is AlarmRingingState.Snoozed) {
        if (alarmRingingState is AlarmRingingState.Snoozed) {
            SnoozedScreen(
                snoozedTimeDisplay = alarmRingingState.snoozedTimeDisplay,
                onFinished = { viewModel.finish() }
            )
        }
    }

    AniVisibility(visible = alarmRingingState is AlarmRingingState.Dismissed) {
        DismissScreen(onFinished = { viewModel.finish() })
    }
}

@Composable
private fun AniVisibility(
    visible: Boolean,
    content: @Composable() AnimatedVisibilityScope.() -> Unit,
) {
    AnimatedVisibility(
        visible = visible, enter = fadeIn(),
        exit = fadeOut(), content = content
    )
}
