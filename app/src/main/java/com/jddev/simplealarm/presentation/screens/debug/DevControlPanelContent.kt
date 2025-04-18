package com.jddev.simpletouch.ui.debug

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun DevControlPanelContent(
    devUtility: DevUtility
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(
            8.dp, Alignment.CenterVertically
        ),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilledTonalButton({
            devUtility.showAlarmNotification()
        }) { Text("Show Alarm notification") }

        Button({
            devUtility.startRingingForegroundService()
        }) { Text("Start Ringing service") }
    }
}