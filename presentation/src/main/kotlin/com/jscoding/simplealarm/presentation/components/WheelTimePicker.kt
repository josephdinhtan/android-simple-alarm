package com.jscoding.simplealarm.presentation.components

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
    is24Hour: Boolean,
    hapticFeedbackEnable: Boolean = false,
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
) {

    var selectedHourIndex by remember { mutableIntStateOf(0) }
    var selectedMinuteIndex by remember { mutableIntStateOf(0) }
    var selectedPeriodIndex by remember { mutableIntStateOf(0) }
    var is24HourFormat by remember { mutableStateOf(is24Hour) }

    val initHourIndex = getAmPmHourIndex(initHour, is24Hour)

    LaunchedEffect(initHour, initMinute, is24Hour) {
        selectedHourIndex = initHourIndex
        selectedMinuteIndex = initMinute
        is24HourFormat = is24Hour
        selectedPeriodIndex = if (initHour > 11) 1 else 0
    }

    val hours = (if (is24HourFormat) (0..23) else (1..12)).toList()    // 12-hour format
    val minutes = (0..59).toList()
    val periods = listOf("AM", "PM")

    val hapticFeedback = LocalHapticFeedback.current

    // Picker Row
    Column {
        Box(
            modifier = Modifier.fillMaxWidth()
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
                TextWheelPicker(
                    items = hours.map { it.toString().padStart(2, '0') },
                    initItemIndex = initHourIndex,
                    onItemSelected = { index, _ ->
                        selectedHourIndex = index
                        onTimeSelected(
                            getHourMilitary(
                                selectedHourIndex, is24HourFormat, selectedPeriodIndex == 1
                            ), selectedMinuteIndex
                        )
                        if (hapticFeedbackEnable) {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                    },
                    wheelTilt = WheelTilt.LEFT,
                    modifier = Modifier.width(80.dp)
                )

                // Minutes Picker
                TextWheelPicker(
                    items = minutes.map { it.toString().padStart(2, '0') },
                    initItemIndex = initMinute,
                    onItemSelected = { index, _ ->
                        selectedMinuteIndex = index
                        onTimeSelected(
                            getHourMilitary(
                                selectedHourIndex, is24HourFormat, selectedPeriodIndex == 1
                            ), selectedMinuteIndex
                        )
                        if (hapticFeedbackEnable) {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                    },
                    wheelTilt = WheelTilt.CENTER,
                    modifier = Modifier.width(80.dp)
                )

                if (!is24Hour) {
                    TextWheelPicker(
                        items = periods,
                        initItemIndex = if (initHour > 11) 1 else 0,
                        onItemSelected = { index, _ ->
                            selectedPeriodIndex = index
                            onTimeSelected(
                                getHourMilitary(
                                    selectedHourIndex, is24HourFormat, selectedPeriodIndex == 1
                                ), selectedMinuteIndex
                            )
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

private fun getAmPmHourIndex(hour: Int, is24Hour: Boolean): Int {
    return if (is24Hour) {
        hour
    } else {
        if (hour == 0 || hour == 12) 11 else (hour % 12 - 1)
    }
}

private fun getHourMilitary(hourIndex: Int, is24Hour: Boolean, isPm: Boolean = false): Int {
    return if (is24Hour) {
        hourIndex
    } else {
        if (isPm) {
            if (hourIndex == 11) 12 else hourIndex + 13
        } else {
            if (hourIndex == 11) 0
            else hourIndex + 1
        }
    }
}

@Composable
@StUiPreview
private fun Preview() {
    StUiPreviewWrapper {
        WheelTimePicker(initHour = 9, initMinute = 0, is24Hour = false, onTimeSelected = { _, _ ->
        })
    }
}
