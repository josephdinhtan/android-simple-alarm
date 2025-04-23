import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jddev.simpletouch.ui.utils.StUiPreview
import com.jddev.simpletouch.ui.utils.StUiPreviewWrapper
import kotlin.math.abs
import kotlin.math.pow

@Composable
fun WheelPicker(
    items: List<String>,
    state: LazyListState,
    onScrollFinished: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    visibleItemsCount: Int = 5,
) {
    val itemHeight = 36.dp
    val itemHeightPx = with(LocalDensity.current) { itemHeight.toPx() }
    val fixItems = listOf("","", "") + items + listOf("","", "")

    val snappingFlingBehavior = rememberSnapFlingBehavior(lazyListState = state)
    val coroutineScope = rememberCoroutineScope()

    // Scroll listener to detect when the center item is snapped
    LaunchedEffect(state) {
        snapshotFlow {
            state.isScrollInProgress
        }.collect { isScrolling ->
            if (!isScrolling) {
                val layoutInfo = state.layoutInfo
                val center = layoutInfo.viewportEndOffset / 2
                val centerItem = layoutInfo.visibleItemsInfo.minByOrNull {
                    abs((it.offset + it.size / 2) - center)
                }
                centerItem?.let {
                    val index = it.index - 3 // Remove padding offset
                    if (index in items.indices) {
                        onScrollFinished(index)
                    }
                }
            }
        }
    }

    Box(
        modifier = modifier
            .height(itemHeight * visibleItemsCount)
            .clipToBounds()
    ) {
        LazyColumn(
            state = state,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy((-8).dp, Alignment.CenterVertically),
            flingBehavior = snappingFlingBehavior
        ) {
            itemsIndexed(fixItems) { index, item ->
                val layoutInfo = state.layoutInfo
                val center = layoutInfo.viewportEndOffset / 2
                val itemInfo = layoutInfo.visibleItemsInfo.find { it.index == index }
                val distance = itemInfo?.let {
                    abs((it.offset + it.size / 2) - center).toFloat()
                } ?: 0f

                val scale = 1f - (distance / itemHeightPx / 6f).coerceIn(0f, 0.3f)
                val alpha = 1f - (distance / itemHeightPx / 3f).coerceIn(0f, 0.6f)

                // New: simulate curvature and compression
                val maxRotation = 35f
                val rotationX = (distance / itemHeightPx).coerceIn(0f, 1f) * maxRotation
                val maxTranslationY = 25f
                val compressionFactor = (distance / itemHeightPx).coerceIn(0f, 1f)
                val translationY = maxTranslationY * compressionFactor * compressionFactor

                Box(Modifier
                    .height(itemHeight)
                    .graphicsLayer {
                        this.rotationX =
                            if (itemInfo != null && (itemInfo.offset + itemInfo.size / 2) < center) -rotationX else rotationX
                        this.translationY =
                            if (itemInfo != null && (itemInfo.offset + itemInfo.size / 2) < center) -translationY else translationY
                        this.scaleX = scale
                        this.scaleY = scale
                        this.alpha = alpha
                        this.cameraDistance = 16 * density
                    }
//                    .background(Color.Gray)
                ) {
                    Text(
                        text = item,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
@StUiPreview
private fun Preview() {
    val state = rememberLazyListState(initialFirstVisibleItemIndex = 2) // skip 2 padding items
    val items = (1..100).map { it.toString() }.toList()

    var selected by remember { mutableStateOf("") }
    StUiPreviewWrapper {
        Text("selected: $selected")
        WheelPicker(
            items = items,
            state = state,
            onScrollFinished = { selectedIndex ->
                selected = items[selectedIndex]
            }
        )
    }
}