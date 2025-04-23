package com.jscoding.simplealarm.presentation.screens.ringing

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AlarmRingingScreen(
    viewModel: AlarmRingingViewmodel = hiltViewModel(),
    alarmId: Long,
    onFinished: () -> Unit,
) {
    LaunchedEffect(alarmId) {
        viewModel.setupAlarm(alarmId)
    }
    val alarmKlaxonState = viewModel.alarmKlaxonState.collectAsState().value
    val shouldFinish = viewModel.shouldFinish.collectAsState().value
    LaunchedEffect(shouldFinish) {
        if (shouldFinish) {
            onFinished()
        }
    }
    AniVisibility(visible = alarmKlaxonState is AlarmKlaxonState.Ringing) {
        if (alarmKlaxonState is AlarmKlaxonState.Ringing) {
            RingingScreen(
                time = alarmKlaxonState.timeDisplay,
                label = alarmKlaxonState.label,
                onSnooze = { viewModel.snoozeAlarm(alarmId) },
                onDismiss = { viewModel.dismissAlarm(alarmId) }
            )
        }
    }

    AniVisibility(visible = alarmKlaxonState is AlarmKlaxonState.Snoozed) {
        if (alarmKlaxonState is AlarmKlaxonState.Snoozed) {
            SnoozedScreen(
                snoozedTimeDisplay = alarmKlaxonState.snoozedTimeDisplay,
                onFinished = { viewModel.finish() }
            )
        }
    }

    AniVisibility(visible = alarmKlaxonState is AlarmKlaxonState.Dismissed) {
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
