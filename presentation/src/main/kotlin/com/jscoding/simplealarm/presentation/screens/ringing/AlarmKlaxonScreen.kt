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
fun AlarmKlaxonScreen(
    viewModel: AlarmKlaxonViewmodel = hiltViewModel(),
    alarmId: Long,
    onFinished: () -> Unit,
) {
    LaunchedEffect(alarmId) {
        viewModel.setupAlarm(alarmId)
    }
    val alarmKlaxonState = viewModel.alarmKlaxonState.collectAsState().value

    AniVisibility(visible = alarmKlaxonState is AlarmKlaxonState.Ringing) {
        RingingScreen(
            time = (alarmKlaxonState as AlarmKlaxonState.Ringing).timeStringDisplay,
            onSnooze = { viewModel.snoozeAlarm(alarmId) },
            onDismiss = { viewModel.dismissAlarm(alarmId) }
        )
    }

    AniVisibility(visible = alarmKlaxonState is AlarmKlaxonState.Snoozed) {
        SnoozedScreen(onFinished = onFinished)
    }

    AniVisibility(visible = alarmKlaxonState is AlarmKlaxonState.Dismissed) {
        DismissScreen(onFinished = onFinished)
    }
}

@Composable
private fun AniVisibility(
    visible: Boolean,
    content: @Composable() AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible, enter = fadeIn(),
        exit = fadeOut(), content = content
    )
}
