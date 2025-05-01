package com.jscoding.simplealarm.presentation.screens.settings.silenceafter

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.jddev.simpletouch.ui.utils.StUiPreview
import com.jddev.simpletouch.ui.utils.StUiPreviewWrapper
import com.jscoding.simplealarm.presentation.components.SimpleSelectionDialog
import com.jscoding.simplealarm.presentation.components.SimpleTextListSelection

private val defaultSilenceAfterLengths = listOf(1, 5, 10, 15, 20, 25)

@Composable
fun SilenceAfterDialog(
    showDialog: Boolean,
    selectedSilenceAfterValue: Int,
    silenceAfterLengths: List<Int> = defaultSilenceAfterLengths,
    onDismissRequest: () -> Unit,
    onSelected: (value: Int) -> Unit,
) {
    val silenceAfterLengthsValues by remember { mutableStateOf(silenceAfterLengths.map { "$it minutes" } + "Never") }
    var selectedIndex = defaultSilenceAfterLengths.indexOf(selectedSilenceAfterValue)
    if (selectedIndex == -1) selectedIndex = silenceAfterLengthsValues.lastIndex
    SimpleSelectionDialog(
        modifier = Modifier.wrapContentHeight(),
        title = "Silence After",
        showDialog = showDialog,
        onDismissRequest = onDismissRequest,
    ) {
        SimpleTextListSelection(
            modifier = Modifier
                .fillMaxWidth(),
            selectedIndex = selectedIndex,
            snoozeLengthsValues = silenceAfterLengthsValues,
            onIndexSelected = { index ->
                if (index == silenceAfterLengthsValues.lastIndex) {
                    onSelected(-1)
                } else {
                    onSelected(silenceAfterLengths[index])
                }
            }
        )
    }
}

@StUiPreview
@Composable
private fun Preview() {
    StUiPreviewWrapper {
        SilenceAfterDialog(
            showDialog = true,
            selectedSilenceAfterValue = 5,
            onDismissRequest = {},
            onSelected = {}
        )
    }
}