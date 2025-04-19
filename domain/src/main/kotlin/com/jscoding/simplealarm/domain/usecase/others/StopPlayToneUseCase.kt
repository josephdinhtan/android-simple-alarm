package com.jscoding.simplealarm.domain.usecase.others

import com.jscoding.simplealarm.domain.platform.MediaPlayer
import com.jscoding.simplealarm.domain.usecase.SuspendUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StopPlayToneUseCase @Inject constructor(
    private val mediaPlayer: MediaPlayer,
) : SuspendUseCase<Unit, Unit> {
    override suspend fun invoke(params: Unit) {
        mediaPlayer.stop()
    }
}