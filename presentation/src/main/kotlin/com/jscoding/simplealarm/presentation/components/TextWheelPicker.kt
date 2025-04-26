package com.jscoding.simplealarm.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jddev.simpletouch.ui.utils.StUiPreview
import com.jddev.simpletouch.ui.utils.StUiPreviewWrapper

@Composable
fun TextWheelPicker(
    modifier: Modifier = Modifier,
    items: List<String>,
    initItemIndex: Int = 0,
    itemHeight: Dp = 50.dp,
    visibleItemCount: Int = 5,
    emphasizeSelectedItem: Boolean = true,
    selectedItemColor: Color = MaterialTheme.colorScheme.onSurface,
    unSelectedItemColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
    wheelTilt: WheelTilt = WheelTilt.CENTER,
    hapticFeedbackEnabled: Boolean = true,
    onItemSelected: (index: Int, item: String) -> Unit,
) {
    WheelPicker(
        modifier = modifier,
        items = items,
        initItemIndex = initItemIndex,
        itemHeight = itemHeight,
        visibleItemCount = visibleItemCount,
        wheelTilt = wheelTilt,
        hapticFeedbackEnabled = hapticFeedbackEnabled,
        onItemSelected = onItemSelected,
    ) { _, item, isSelectedItem ->
        BasicText(
            text = item,
            style = TextStyle(
                fontSize = if (isSelectedItem && emphasizeSelectedItem) 24.sp else 20.sp,
                fontWeight = if (isSelectedItem) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelectedItem) selectedItemColor else unSelectedItemColor,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun PreviewWheelPicker() {
    val items = (1..20).map { "Item $it" }
    var selectedItem by remember { mutableStateOf("Item 3") }
    var selectedIndex by remember { mutableIntStateOf(0) }
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(30.dp))
        BasicText(
            text = "Selected index: $selectedIndex, item: $selectedItem",
            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary),
        )
        TextWheelPicker(
            items = items,
            initItemIndex = 2,
            onItemSelected = { index, item ->
                selectedItem = item
                selectedIndex = index
            },
            modifier = Modifier
                .height(250.dp)
                .width(150.dp)
        )
    }
}

@Composable
@StUiPreview
private fun Preview() {
    StUiPreviewWrapper {
        PreviewWheelPicker()
    }
}