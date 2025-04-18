package com.jscoding.simplealarm.domain.usecase.others

import android.net.Uri
import com.jscoding.simplealarm.domain.model.alarm.Ringtone
import com.jscoding.simplealarm.domain.platform.MediaPlayer
import com.jscoding.simplealarm.domain.platform.SystemSettingsManager
import com.jscoding.simplealarm.domain.usecase.SuspendUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StartPlayToneUseCase @Inject constructor(
    private val mediaPlayer: MediaPlayer,
    private val systemSettingsManager: SystemSettingsManager,
) : SuspendUseCase<Ringtone, Unit> {
    override suspend fun invoke(params: Ringtone) {
        if (params.uri == Uri.EMPTY) return
        val maxVolume = systemSettingsManager.getMaxAlarmVolume().toFloat()
        val volume = systemSettingsManager.getAlarmVolume().toFloat()
        mediaPlayer.play(params.uri, volume / maxVolume)
    }
}