package com.jscoding.simplealarm.presentation.components

import WheelPicker
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.jddev.simpletouch.ui.utils.StUiPreview
import com.jddev.simpletouch.ui.utils.StUiPreviewWrapper
import timber.log.Timber

@Composable
fun WheelTimePicker(
    initHour: Int,
    initMinute: Int,
    is24Hour: Boolean = true,
    hapticFeedbackEnable: Boolean = false,
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
) {
    val hours = (if (is24Hour) (0..23) else (1..12)).toList()    // 12-hour format
    val minutes = (0..59).toList()
    val periods = listOf("AM", "PM")

    var selectedHourIndex by remember { mutableIntStateOf(0) }
    var selectedMinuteIndex by remember { mutableIntStateOf(0) }
    var selectedPeriodIndex by remember { mutableIntStateOf(0) }

    val hapticFeedback = LocalHapticFeedback.current

    // Picker Row
    Column {
        Text("${hours[selectedHourIndex]}:${minutes[selectedMinuteIndex]} ${periods[selectedPeriodIndex]}")
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Selection Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .align(Alignment.Center)
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f))
                    .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f))
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hours Picker
                WheelPicker(
                    items = hours.map { it.toString().padStart(2, '0') },
                    onItemSelected = { index ->
                        selectedHourIndex = index
                        if (hapticFeedbackEnable) {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                    },
                    modifier = Modifier.width(80.dp)
                )

                // Minutes Picker
                WheelPicker(
                    items = minutes.map { it.toString().padStart(2, '0') },
                    onItemSelected = { index ->
                        selectedMinuteIndex = index
                        if (hapticFeedbackEnable) {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                    },
                    modifier = Modifier.width(80.dp)
                )

                if (!is24Hour) {
                    WheelPicker(
                        items = periods,
                        initItemIndex = 0,
                        onItemSelected = { index ->
                            selectedPeriodIndex = index

                            Timber.d("Joseph selectedPeriodIndex: $index")
                            if (hapticFeedbackEnable) {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        },
                        wheelTilt = WheelTilt.RIGHT,
                        visibleItemCount = 3,
                        modifier = Modifier.width(80.dp)
                    )
                }
            }
        }
    }
}

@Composable
@StUiPreview
private fun Preview() {
    StUiPreviewWrapper {
        WheelTimePicker(
            initHour = 9,
            initMinute = 0,
            is24Hour = false,
            onTimeSelected = { hour, minute ->
            }
        )
    }
}
