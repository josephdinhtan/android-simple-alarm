package com.jddev.simplealarm.presentation.screens.alarm

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import com.jddev.simpletouch.ui.foundation.topappbar.StUiLargeTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onAddClick: () -> Unit,
    navigateToSettings: () -> Unit,
) {
    StUiLargeTopAppBar(
        title = "Alarm",
        scrollBehavior = scrollBehavior,
        actions = {
            IconButton(onClick = navigateToSettings) {
                Icon(Icons.Outlined.Settings, contentDescription = "Settings")
            }
            IconButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Alarm")
            }
        }
    )
}