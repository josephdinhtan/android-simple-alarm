package com.jscoding.simplealarm.presentation.screens.ringing

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.jddev.simpletouch.ui.utils.StUiPreview
import com.jddev.simpletouch.ui.utils.StUiPreviewWrapper
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RingingScreen(
    time: String,
    label: String,
    onSnooze: () -> Unit,
    onDismiss: () -> Unit,
) {
    Surface(
        Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .systemBarsPadding()
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = time,
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                if (label.isNotBlank()) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))

            SlideActionTrack(
                onSnooze = onSnooze,
                onDismiss = onDismiss
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

//class VibratorCompat(private val context: Context) {
//    fun vibrate() {
//        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
//        vibrator?.let {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                it.vibrate(
//                    VibrationEffect.createWaveform(longArrayOf(0, 500, 500), 0)
//                )
//            } else {
//                it.vibrate(longArrayOf(0, 500, 500), 0)
//            }
//        }
//    }
//}

@Composable
fun SlideActionTrack(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(50),
    trackWidth: Dp = 300.dp,
    ballHeight: Dp = 84.dp,
    ballWidth: Dp = 100.dp,
    onSnooze: () -> Unit,
    onDismiss: () -> Unit,
) {
    val offsetX = remember { Animatable(0f) }
    val maxOffsetPx = with(LocalDensity.current) { (trackWidth - ballWidth).toPx() / 2 }

    Box(
        modifier = modifier.background(Color.LightGray.copy(alpha = 0.3f), shape)
    ) {
        Text(
            "Snooze",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            "Dismiss", modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            style = MaterialTheme.typography.bodyMedium
        )
        Box(
            Modifier
                .width(trackWidth)
                .height(ballHeight)
                .padding(6.dp)
                .pointerInput(Unit) {
                    coroutineScope {
                        detectDragGestures(onDragEnd = {
                            launch {
                                when {
                                    offsetX.value > maxOffsetPx * 0.6f -> {
                                        offsetX.animateTo(maxOffsetPx)
                                        delay(500)
                                        onDismiss()
                                    }

                                    offsetX.value < -maxOffsetPx * 0.6f -> {
                                        offsetX.animateTo(-maxOffsetPx)
                                        delay(500)
                                        onSnooze()
                                    }

                                    else -> offsetX.animateTo(0f)
                                }
                            }
                        }, onDrag = { change, dragAmount ->
                            change.consume()
                            launch {
                                val newOffset = (offsetX.value + dragAmount.x).coerceIn(
                                    -maxOffsetPx, maxOffsetPx
                                )
                                offsetX.snapTo(newOffset)
                            }
                        })
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier
                    .height(ballHeight)
                    .width(ballWidth)
                    .offset { IntOffset(offsetX.value.toInt(), 0) }
                    .background(Color(0xFFEAA421), shape)
            ) {
                Icon(
                    Icons.Default.Alarm,
                    contentDescription = "Dismiss",
                    tint = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@StUiPreview
@Composable
private fun Preview() {
    StUiPreviewWrapper {
        RingingScreen(
            time = "00:00",
            label = "Wake up",
            onSnooze = {},
            onDismiss = {}
        )
    }
}