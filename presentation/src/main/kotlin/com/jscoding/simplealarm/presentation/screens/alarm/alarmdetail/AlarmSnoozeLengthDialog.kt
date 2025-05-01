package com.jscoding.simplealarm.presentation.screens.alarm.alarmdetail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.jddev.simpletouch.ui.utils.StUiPreview
import com.jddev.simpletouch.ui.utils.StUiPreviewWrapper
import com.jscoding.simplealarm.presentation.components.SimpleSelectionDialog
import com.jscoding.simplealarm.presentation.components.SimpleTextListSelection

private val defaultSnoozeLengths = (1..30).toList()

@Composable
fun AlarmSnoozeLengthDialog(
    showDialog: Boolean,
    selectedSnoozeLength: Int,
    snoozeLengths: List<Int> = defaultSnoozeLengths,
    onDismissRequest: () -> Unit,
    onSelected: (Int) -> Unit,
) {
    val snoozeLengthsValues by remember { mutableStateOf(snoozeLengths.map { "$it minutes" }) }
    SimpleSelectionDialog(
        title = "Snooze Length",
        showDialog = showDialog,
        onDismissRequest = onDismissRequest,
    ) {
        SimpleTextListSelection(
            modifier = Modifier
                .fillMaxSize(),
            selectedIndex = selectedSnoozeLength - 1,
            snoozeLengthsValues = snoozeLengthsValues,
            onIndexSelected = { index ->
                onSelected(snoozeLengths[index])
            }
        )
    }
}


@StUiPreview
@Composable
private fun Preview() {
    StUiPreviewWrapper {
        AlarmSnoozeLengthDialog(
            showDialog = true,
            selectedSnoozeLength = 5,
            onDismissRequest = {},
            onSelected = {}
        )
    }
}