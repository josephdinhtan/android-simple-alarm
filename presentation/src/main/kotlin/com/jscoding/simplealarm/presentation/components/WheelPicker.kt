package com.jscoding.simplealarm.presentation.components

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jddev.simpletouch.ui.utils.StUiPreview
import com.jddev.simpletouch.ui.utils.StUiPreviewWrapper
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import timber.log.Timber
import kotlin.math.absoluteValue

enum class WheelTilt {
    RIGHT, CENTER, LEFT,
}

@OptIn(FlowPreview::class)
@Composable
fun <T> WheelPicker(
    modifier: Modifier = Modifier,
    items: List<T>,
    initItemIndex: Int = 2,
    itemHeight: Dp = 50.dp,
    visibleItemCount: Int = 5,
    wheelTilt: WheelTilt = WheelTilt.CENTER,
    hapticFeedbackEnabled: Boolean = true,
    onItemSelected: (index: Int, item: T) -> Unit,
    itemContent: @Composable (index: Int, item: T, isSelectedItem: Boolean) -> Unit,
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initItemIndex)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val hapticFeedback = LocalHapticFeedback.current

    // State to hold the currently selected index based on UI
    var currentSelectedIndex by remember {
        mutableIntStateOf(
            initItemIndex.coerceIn(
                0, items.size - 1
            )
        )
    }

    LaunchedEffect(initItemIndex) {
        listState.scrollToItem(initItemIndex.coerceIn(0, items.size - 1))
        currentSelectedIndex = initItemIndex.coerceIn(0, items.size - 1)
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }.debounce(50L).filter { !it }.collect {
            val layoutInfo = listState.layoutInfo
            val center = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
            val centerItem = layoutInfo.visibleItemsInfo.minByOrNull {
                ((it.offset + it.size / 2) - center).absoluteValue
            }

            centerItem?.let {
                val index = it.index
                val item = items.getOrNull(index)
                if (item != null && index != currentSelectedIndex) {
                    onItemSelected(index, item)
                    currentSelectedIndex = index
                }
            }
        }
    }

    // LaunchedEffect for haptic feedback
    var previousHapticIndex by remember { mutableIntStateOf(-1) }
    LaunchedEffect(listState, hapticFeedbackEnabled) {
        if (!hapticFeedbackEnabled) return@LaunchedEffect
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val center = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
            layoutInfo.visibleItemsInfo.minByOrNull {
                ((it.offset + it.size / 2) - center).absoluteValue
            }?.index
        }.collectLatest { centeredIndex ->
            if (listState.isScrollInProgress && centeredIndex != null &&
                centeredIndex != previousHapticIndex
            ) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                previousHapticIndex = centeredIndex
            }
        }
    }

    Box(
        modifier = modifier.height(itemHeight * visibleItemCount),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            contentPadding = PaddingValues(vertical = (itemHeight * (visibleItemCount / 2))),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize() // Ensure LazyColumn fills the Box
        ) {
            itemsIndexed(items) { index, item ->
                // Get current layout info
                val layoutInfo = listState.layoutInfo
                val itemInfo = layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }
                val centerY = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
                val distance = itemInfo?.let {
                    val itemCenterY = it.offset + it.size / 2
                    (itemCenterY - centerY).toFloat()
                } ?: 0f

                // Real-time transform
                val scale = (1f - (distance * distance * 0.000005f)).coerceIn(0.4f, 1f)
                val alpha = (1f - (distance * distance * 0.000008f)).coerceIn(0.4f, 1f)
                val rotationX = (distance * distance * 0.0007f).coerceIn(0f, 90f)
                val transitionY = (distance * distance * 0.0009f).coerceIn(0f, 100f)
                val transitionXVolume = (distance * 0.03f).absoluteValue.coerceIn(0f, 15f)
                val transitionX = when (wheelTilt) {
                    WheelTilt.RIGHT -> -transitionXVolume
                    WheelTilt.CENTER -> 0f
                    WheelTilt.LEFT -> transitionXVolume
                }

                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth()
                        .graphicsLayer {
                            this.rotationX = if (distance > 0) -rotationX else rotationX
                            this.translationY = if (distance > 0) -transitionY else transitionY
                            this.translationX = transitionX
                            this.scaleY = scale
                            this.scaleX = scale
                            this.alpha = alpha
                        }
//                        .background(Color.Gray.copy(alpha = 0.3f))
                        .semantics {
                            contentDescription = "Item at index $index with value $item"
                        },
                    contentAlignment = Alignment.Center
                ) {
                    itemContent(index, item, index == currentSelectedIndex)
                }
            }
        }
    }
}

@Composable
@StUiPreview
private fun Preview() {
    StUiPreviewWrapper {
        val listItem = (0..50).map { it.toString() }
        val selectedIndex = remember { mutableStateOf("") }
        Text(selectedIndex.value)
        Column {
            WheelPicker(items = listItem,
                visibleItemCount = 5,
                wheelTilt = WheelTilt.LEFT,
                onItemSelected = { index, _ ->
                    selectedIndex.value = listItem[index]
                }) { _, item, isSelectedItem ->
                BasicText(
                    text = item,
                    style = TextStyle(
                        fontSize = if (isSelectedItem) 32.sp else 30.sp,
                        fontWeight = if (isSelectedItem) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelectedItem) MaterialTheme.colorScheme.onSurface else Color.Gray,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}