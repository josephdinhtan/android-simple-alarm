package com.jscoding.simplealarm.domain.usecase.others

import android.net.Uri
import com.jscoding.simplealarm.domain.entity.alarm.Ringtone
import com.jscoding.simplealarm.domain.platform.SystemSettingsManager
import com.jscoding.simplealarm.domain.platform.TonePlayer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StartPlayToneUseCase @Inject constructor(
    private val mediaPlayer: TonePlayer,
    private val systemSettingsManager: SystemSettingsManager,
) {
    operator fun invoke(ringtone: Ringtone) {
        if (ringtone.uri == Uri.EMPTY) return
        val maxVolume = systemSettingsManager.getMaxAlarmVolume().toFloat()
        val volume = systemSettingsManager.getAlarmVolume().toFloat()
        mediaPlayer.play(ringtone.uri, volume / maxVolume)
    }
}