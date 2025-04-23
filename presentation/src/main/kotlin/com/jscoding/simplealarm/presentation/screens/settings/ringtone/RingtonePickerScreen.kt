package com.jscoding.simplealarm.presentation.screens.settings.ringtone

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.LottieDynamicProperties
import com.airbnb.lottie.compose.LottieDynamicProperty
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.model.KeyPath
import com.jddev.simpletouch.ui.customization.settingsui.StSettingsUi
import com.jddev.simpletouch.ui.customization.settingsui.group.StSettingsGroup
import com.jddev.simpletouch.ui.foundation.StUiCircleCheckbox
import com.jddev.simpletouch.ui.foundation.topappbar.StUiLargeTopAppBar
import com.jddev.simpletouch.ui.foundation.topappbar.stUiLargeTopAppbarScrollBehavior
import com.jddev.simpletouch.ui.utils.StUiPreview
import com.jddev.simpletouch.ui.utils.StUiPreviewWrapper
import com.jscoding.simplealarm.domain.entity.alarm.Ringtone
import com.jscoding.simplealarm.presentation.R

@Composable
fun RingtonePickerScreen(
    title: String,
    alarmId: Long = -1L,
    ringtonePickerViewModel: RingtonePickerViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    val isFromAlarmEditScreen = alarmId != -1L
    LaunchedEffect(Unit) {
        if (isFromAlarmEditScreen) {
            ringtonePickerViewModel.getAlarmSelectedRingtone(alarmId)
        } else {
            ringtonePickerViewModel.getDefaultRingtone()
        }
    }
    RingtonePickerScreen(
        screenTitle = title,
        ringtones = ringtonePickerViewModel.availableRingtones.collectAsState().value,
        isTonePlaying = ringtonePickerViewModel.isTonePlaying.collectAsState().value,
        selectedRingtone = ringtonePickerViewModel.selectedRingtone.collectAsState().value,
        onRingtoneSelected = { ringtonePickerViewModel.onRingtoneSelectedAndPlayTone(it) },
        onStopPlaying = { ringtonePickerViewModel.stopPlayTone() },
        onSave = {
            if (isFromAlarmEditScreen) {
                ringtonePickerViewModel.setAlarmRingtone(
                    ringtonePickerViewModel.selectedRingtone.value, alarmId
                )
            } else {
                ringtonePickerViewModel.setDefaultRingtone(
                    ringtonePickerViewModel.selectedRingtone.value
                )
            }
            ringtonePickerViewModel.stopPlayTone()
            onBack()
        },
        onBack = {
            ringtonePickerViewModel.stopPlayTone()
            onBack()
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RingtonePickerScreen(
    screenTitle: String,
    ringtones: List<Ringtone>,
    selectedRingtone: Ringtone,
    isTonePlaying: Boolean,
    onRingtoneSelected: (Ringtone) -> Unit,
    onStopPlaying: () -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
) {
    BackHandler { onBack() }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP || event == Lifecycle.Event.ON_DESTROY || event == Lifecycle.Event.ON_PAUSE) {
                onStopPlaying()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    val scrollBehavior = stUiLargeTopAppbarScrollBehavior()
    Scaffold(contentWindowInsets = WindowInsets.safeDrawing, topBar = {
        StUiLargeTopAppBar(
            title = screenTitle,
            onBack = onBack,
            scrollBehavior = scrollBehavior,
            actions = {
                IconButton(onClick = {
                    onSave()
                }) {
                    Icon(Icons.Default.Check, contentDescription = "Save")
                }
            },
        )
    }) { innerPadding ->
        StSettingsUi(
            Modifier.padding(innerPadding),
            scrollBehavior = scrollBehavior,
        ) {
            ringtones.forEach { ringtone ->
                StSettingsGroup {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = (ringtone.uri == selectedRingtone.uri), onClick = {
                                    onRingtoneSelected(ringtone)
                                }, role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.NotificationsActive,
                            "Tone",
                            Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
                        )
                        Text(
                            text = ringtone.title,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        LottieRingingAnimation(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            isPlaying = isTonePlaying && (ringtone.uri == selectedRingtone.uri)
                        )
                        SelectedCheckbox(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            isSelected = (ringtone.uri == selectedRingtone.uri)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LottieRingingAnimation(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
) {
    AnimatedVisibility(
        modifier = modifier, visible = isPlaying, enter = fadeIn(), exit = fadeOut()
    ) {
        val color = MaterialTheme.colorScheme.onBackground

        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ringing))
        val progress by animateLottieCompositionAsState(
            composition, iterations = LottieConstants.IterateForever
        )

        val dynamicProps = remember(color) {
            LottieDynamicProperties(
                listOf(
                    LottieDynamicProperty(
                        property = LottieProperty.STROKE_COLOR,
                        value = color.toArgb(),
                        keyPath = KeyPath("**") // match all strokes
                    ), LottieDynamicProperty(
                        property = LottieProperty.COLOR,
                        value = color.toArgb(),
                        keyPath = KeyPath("**") // match all fills (just in case)
                    )
                )
            )
        }

        LottieAnimation(
            composition = composition,
            progress = { progress },
            dynamicProperties = dynamicProps,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun SelectedCheckbox(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
) {
    AnimatedVisibility(
        modifier = modifier, visible = isSelected, enter = fadeIn(), exit = fadeOut()
    ) {
        StUiCircleCheckbox(
            checked = true, onCheckedChange = null
        )
    }
}

@Composable
@StUiPreview
private fun Preview() {
    val ringtones = List(
        20
    ) { Ringtone("Ringtone $it", Uri.parse("uri_test_$it")) }
    StUiPreviewWrapper {
        RingtonePickerScreen(
            screenTitle = "Ringtone",
            isTonePlaying = true,
            ringtones = ringtones,
            selectedRingtone = Ringtone("Ringtone 5", Uri.parse("uri_test_5")),
            onRingtoneSelected = {},
            onStopPlaying = {},
            onSave = {},
            onBack = {},
        )
    }
}