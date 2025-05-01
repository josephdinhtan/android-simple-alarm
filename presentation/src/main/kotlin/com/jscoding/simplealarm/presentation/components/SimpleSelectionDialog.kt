package com.jscoding.simplealarm.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jddev.simpletouch.ui.foundation.dialog.StUiBaseDialog

@Composable
fun SimpleSelectionDialog(
    modifier: Modifier = Modifier,
    title: String,
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    StUiBaseDialog(
        showDialog = showDialog,
        onDismissRequest = onDismissRequest
    ) {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        val screenHeight = configuration.screenHeightDp.dp

        val dialogWidth = screenWidth * 0.85f
        val maxDialogHeight = screenHeight * 0.8f
        Surface(
            modifier = modifier
                .widthIn(max = 500.dp)
                .width(dialogWidth)
                .heightIn(max = maxDialogHeight),
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 32.dp),
                    text = title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.W700, textAlign = TextAlign.Center
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                HorizontalDivider()
                Box(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .padding(vertical = 4.dp)
                ) {
                    content()
                }
                HorizontalDivider()
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            onDismissRequest()
                        }, contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 16.dp, horizontal = 32.dp),
                        text = "Cancel",
                        style = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}