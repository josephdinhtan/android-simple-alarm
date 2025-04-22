package com.jscoding.simplealarm.domain.usecase.others

import com.jscoding.simplealarm.domain.platform.MediaPlayer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StopPlayToneUseCase @Inject constructor(
    private val mediaPlayer: MediaPlayer,
) {
    operator fun invoke() {
        mediaPlayer.stop()
    }
}