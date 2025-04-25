package com.jscoding.simplealarm.presentation.components

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jddev.simpletouch.ui.utils.StUiPreview
import com.jddev.simpletouch.ui.utils.StUiPreviewWrapper
import kotlin.math.absoluteValue

enum class WheelTilt {
    RIGHT,
    CENTER,
    LEFT,
}

@Composable
fun WheelPicker(
    modifier: Modifier = Modifier,
    items: List<String>,
    initItemIndex: Int = 2,
    itemHeight: Int = 50,
    visibleItemCount: Int = 5,
    wheelTilt: WheelTilt = WheelTilt.CENTER,
    onItemSelected: (index: Int) -> Unit,
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initItemIndex)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    LaunchedEffect(listState) {
        snapshotFlow {
            listState.isScrollInProgress
        }.collect { isScrolling ->
            if (!isScrolling) {
                val layoutInfo = listState.layoutInfo
                val center = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
                val centerItem = layoutInfo.visibleItemsInfo.minByOrNull {
                    ((it.offset + it.size / 2) - center).absoluteValue
                }

                centerItem?.let {
                    val index = it.index
                    if (index in items.indices) {
                        onItemSelected(index)
                    }
                }
            }
        }
    }

    Box(
        modifier = modifier
            .height((itemHeight * visibleItemCount).dp),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            contentPadding = PaddingValues(vertical = (itemHeight * (visibleItemCount / 2)).dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
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
                val scale = 1f - (distance * distance * 0.0008f * 0.01f).coerceIn(0f, 0.3f)
                val alpha = 1f - (distance * distance * 0.0008f * 0.01f).coerceIn(0f, 0.6f)
                val rotationX = (distance * distance * 0.0007f).coerceIn(0f, 90f)

                val transitionY = (distance * distance * 0.0009f).coerceIn(0f, 100f)

                val transitionXVolume = (distance * 0.03f).absoluteValue.coerceIn(0f, 10f)
                val transitionX = when(wheelTilt) {
                    WheelTilt.RIGHT -> {
                        transitionXVolume
                    }
                    WheelTilt.CENTER -> {
                        0f
                    }
                    WheelTilt.LEFT -> {
                        -transitionXVolume
                    }
                }

                Box(
                    modifier = Modifier
                        .height(itemHeight.dp)
                        .graphicsLayer {
                            this.rotationX = if (distance > 0) -rotationX else rotationX
                            this.translationY = if (distance > 0) -transitionY else transitionY
                            this.translationX = -transitionX
                            this.scaleY = scale
                            this.scaleX = scale
                            this.alpha = alpha
                        }
//                        .background(Color.Gray.copy(alpha = 0.4f))
                    ,
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface/*.copy(alpha = alpha)*/,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
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
        val listItem = (0..50).map { it.toString() }
        val selectedIndex = remember { mutableStateOf("") }
        Text(selectedIndex.value)
        Column {
            WheelPicker(
                items = listItem,
                visibleItemCount = 5,
                wheelTilt = WheelTilt.LEFT,
                onItemSelected = { index ->
                    selectedIndex.value = listItem[index]
                }
            )
        }
    }
}