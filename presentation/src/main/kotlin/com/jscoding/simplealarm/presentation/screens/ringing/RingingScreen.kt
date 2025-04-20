package com.jscoding.simplealarm.presentation.screens.ringing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jddev.simpletouch.ui.utils.StUiPreview
import com.jddev.simpletouch.ui.utils.StUiPreviewWrapper

@Composable
fun RingingScreen(
    time: String,
    label: String = "",
    onSnooze: () -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
//    val vibration = remember { VibratorCompat(context) }
//
//    // Trigger vibration (optional)
//    LaunchedEffect(Unit) {
//        vibration.vibrate()
//    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 32.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

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
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RingingButton(
                    text = "Snooze",
                    icon = Icons.Default.Snooze,
                    onClick = onSnooze,
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer
                )
                RingingButton(
                    text = "Dismiss",
                    icon = Icons.Default.Close,
                    onClick = onDismiss,
                    backgroundColor = MaterialTheme.colorScheme.errorContainer
                )
            }
        }
    }
}

@Composable
fun RingingButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    backgroundColor: Color,
) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        modifier = Modifier
            .size(120.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = text, modifier = Modifier.size(32.dp))
            Text(text)
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

@StUiPreview
@Composable
private fun Preview() {
    StUiPreviewWrapper {
        RingingScreen(
            time = "00:00",
            onSnooze = {},
            onDismiss = {}
        )
    }
}