package com.jddev.simplealarm.domain.usecase.others

import com.jddev.simplealarm.domain.system.MediaPlayer
import com.jddev.simplealarm.domain.usecase.SuspendUseCase
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