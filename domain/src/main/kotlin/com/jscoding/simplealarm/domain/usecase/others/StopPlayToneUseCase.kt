package com.jscoding.simplealarm.domain.usecase.others

import com.jscoding.simplealarm.domain.platform.TonePlayer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StopPlayToneUseCase @Inject constructor(
    private val mediaPlayer: TonePlayer,
) {
    operator fun invoke() {
        mediaPlayer.stop()
    }
}