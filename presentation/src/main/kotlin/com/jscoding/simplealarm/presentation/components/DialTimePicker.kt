package com.jscoding.simplealarm.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jddev.simpletouch.ui.utils.StUiPreview
import com.jddev.simpletouch.ui.utils.StUiPreviewWrapper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialTimePicker(
    modifier: Modifier = Modifier,
    initialHour: Int,
    initialMinute: Int,
    is24Hour: Boolean = true,
    onConfirm: (TimePickerState) -> Unit,
    onDismiss: () -> Unit,
) {
    var isDialMethode by remember { mutableStateOf(true) }

    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = is24Hour,
    )

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        AnimatedVisibility(isDialMethode) {
            TimePicker(state = timePickerState)
        }
        AnimatedVisibility(!isDialMethode) {
            TimeInput(state = timePickerState)
        }
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { isDialMethode = !isDialMethode }) {
                AnimatedVisibility(isDialMethode) {
                    Icon(Icons.Outlined.Keyboard, "Keyboard")
                }
                AnimatedVisibility(!isDialMethode) {
                    Icon(Icons.Outlined.AccessTime, "Dial")
                }
            }
            Spacer(Modifier.weight(1f))
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
            Spacer(Modifier.width(8.dp))
            TextButton(onClick = { onConfirm(timePickerState) }) {
                Text("OK")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@StUiPreview
@Composable
private fun PreviewDial() {
    StUiPreviewWrapper {
        DialTimePicker(Modifier, 12, 0, true, {}, {})
    }
}