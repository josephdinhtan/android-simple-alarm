package com.jddev.simplealarm.presentation.screens.alarm

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jddev.simplealarm.domain.model.Alarm
import com.jddev.simpletouch.ui.foundation.StUiSwitch
import com.jddev.simpletouch.ui.utils.StUiPreview
import com.jddev.simpletouch.ui.utils.StUiPreviewWrapper
import java.time.DayOfWeek

@Composable
fun AlarmItem(
    modifier: Modifier = Modifier,
    alarm: Alarm,
    onToggle: (Boolean) -> Unit,
    onClick: () -> Unit,
) {
    val transition = updateTransition(alarm.isEnabled, label = "alarm_toggle")
    val elevation by transition.animateDp(label = "elevation") {
        if (it) 6.dp else 2.dp
    }
    val repeatDayColor by transition.animateColor(label = "repeat_day_text_color") {
        if (it) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    }
    val textAlpha by transition.animateFloat(label = "text_alpha") {
        if (it) 1f else 0.5f
    }

    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(
                alpha = 0.5f
            )
        ),
    ) {
        Row(modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${alarm.hour.toString().padStart(2, '0')}:${
                        alarm.minute.toString().padStart(2, '0')
                    }",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = textAlpha),
                    modifier = Modifier.padding(end = 16.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                alarm.label.takeIf { it.isNotBlank() }?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = textAlpha),
                        modifier = Modifier.padding(end = 16.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (alarm.repeatDays.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = alarm.repeatDays.joinToString(", ") {
                            it.toString().take(3)
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = repeatDayColor.copy(alpha = textAlpha),
                        modifier = Modifier.padding(end = 16.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                StUiSwitch(
                    checked = alarm.isEnabled,
                    onCheckedChange = onToggle,
                )

                IconButton(onClick = onClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Edit Alarm"
                    )
                }
            }
        }
    }
}

@StUiPreview
@Composable
private fun Preview() {
    val repeatDayOfWeeK = listOf(
        DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY
    )
    StUiPreviewWrapper {
        AlarmItem(alarm = Alarm(
            1,
            12,
            0,
            "Test overflow line, Test overflow line, Test overflow line",
            repeatDayOfWeeK
        ), onToggle = {}, onClick = {})
        Spacer(Modifier.height(16.dp))
        AlarmItem(alarm = Alarm(
            1, 12, 0, "Test", repeatDayOfWeeK, isEnabled = false
        ), onToggle = {}, onClick = {})
    }
}