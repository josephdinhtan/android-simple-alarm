package com.jscoding.simplealarm.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jddev.simpletouch.ui.foundation.StUiCircleCheckbox

@Composable
fun SelectedCheckbox(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
) {
    AnimatedVisibility(
        modifier = modifier, visible = isSelected, enter = fadeIn(), exit = fadeOut()
    ) {
        StUiCircleCheckbox(
            checked = true, onCheckedChange = null
        )
    }
}