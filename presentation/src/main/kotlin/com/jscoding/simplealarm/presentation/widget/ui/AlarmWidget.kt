package com.jscoding.simplealarm.presentation.widget.ui

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.jscoding.simplealarm.domain.entity.alarm.DayOfWeek
import com.jscoding.simplealarm.presentation.utils.toAmPmNotationStr
import com.jscoding.simplealarm.presentation.utils.toDisplayString
import com.jscoding.simplealarm.presentation.utils.toStringTimeDisplay
import com.jscoding.simplealarm.presentation.widget.model.AlarmWidgetModel

@Composable
fun AlarmWidget(
    modifier: GlanceModifier = GlanceModifier,
    alarm: AlarmWidgetModel,
    is24hFormat: Boolean,
    onAlarmChange: (AlarmWidgetModel) -> Unit,
    onClick: () -> Unit,
) {
    val transition = updateTransition(alarm.enabled, label = "alarm_toggle")
    val elevation by transition.animateDp(label = "elevation") {
        if (it) 6.dp else 2.dp
    }
    val repeatDayColor by transition.animateColor(label = "repeat_day_text_color") {
        if (it) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    }
    val textAlpha by transition.animateFloat(label = "text_alpha") {
        if (it) 1f else 0.5f
    }
    Column(
        modifier = modifier.fillMaxSize().clickable(onClick)
            .background(GlanceTheme.colors.widgetBackground).appWidgetBackground()
            .padding(bottom = 8.dp),
        verticalAlignment = androidx.glance.layout.Alignment.Vertical.Bottom,
        horizontalAlignment = androidx.glance.layout.Alignment.Horizontal.CenterHorizontally,
    ) {
        if (alarm.repeatDays.isNotEmpty()) {
            Spacer(modifier = GlanceModifier.height(4.dp))
            Text(
                text = alarm.repeatDays.toDisplayString(),
                modifier = GlanceModifier.padding(end = 16.dp),
                maxLines = 1,
            )
        }
        Row(
            GlanceModifier.fillMaxWidth(),
            verticalAlignment = androidx.glance.layout.Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = GlanceModifier.padding(end = 16.dp),
                verticalAlignment = androidx.glance.layout.Alignment.Bottom
            ) {
                Text(
                    text = alarm.toStringTimeDisplay(is24hFormat),
                    maxLines = 1,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = (GlanceTheme.colors.onSurface),
                    )
                )

                if (!is24hFormat) {
                    Spacer(modifier = GlanceModifier.width(4.dp))
                    Text(
                        text = alarm.toAmPmNotationStr(),
//                        style = MaterialTheme.typography.headlineLarge.copy(
//                            fontWeight = FontWeight.Bold,
//                            fontSize = 24.sp
//                        ),
//                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = textAlpha),
                    )
                }
            }

//            StUiSwitch(
//                modifier = Modifier.padding(8.dp),
//                checked = alarm.enabled,
//                onCheckedChange = onToggle,
//            )
        }
        alarm.label.takeIf { it.isNotBlank() }?.let {
            Text(
                text = it,
//                style = MaterialTheme.typography.titleMedium,
//                color = MaterialTheme.colorScheme.onSurface.copy(alpha = textAlpha),
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 200, heightDp = 150)
@Composable
private fun Preview() {
    val alarm = AlarmWidgetModel (
        id = 0L,
        hour = 6,
        minute = 0,
        label = "label",
        repeatDays = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
        enabled = true,
    )

    GlanceTheme {
        AlarmWidget(
            alarm = alarm,
            is24hFormat = true,
            onAlarmChange = {},
            onClick = {}
        )
    }
}