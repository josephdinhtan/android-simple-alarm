package com.jddev.simplealarm.platform.helper

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import com.jddev.simplealarm.platform.di.CoroutineScopeMain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration

class MediaPlayerHelper @Inject constructor(
    private val mediaPlayer: MediaPlayer,
    @CoroutineScopeMain private val coroutineScopeMain: CoroutineScope,
    private val context: Context,
) {
    fun play(uri: Uri, volume: Float, volumeFadeDuration: Duration = Duration.ZERO) {
        mediaPlayer.apply {
            setDataSource(context, uri)
            setAudioStreamType(AudioManager.STREAM_ALARM)
            isLooping = true
            setVolume(volume.coerceIn(0.0f..1.0f), volume.coerceIn(0.0f..1.0f))
            prepare()
            start()
        }

        // Fade-in volume
        if (volumeFadeDuration != Duration.ZERO) {
            coroutineScopeMain.launch {
                fadeInVolume(mediaPlayer, volumeFadeDuration.inWholeMilliseconds)
            }
        }
    }

    fun stop() {
        try {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.reset() // Safe to call in any state
        } catch (e: IllegalStateException) {
            // Log the error for debugging, but don't crash
            Timber.e("Error stopping MediaPlayer: ${e.message}")
        }
    }

    private suspend fun fadeInVolume(player: MediaPlayer, durationMillis: Long) {
        val steps = 50 // smoothness
        val delayPerStep = durationMillis / steps
        for (i in 1..steps) {
            val volume = i / steps.toFloat()
            player.setVolume(volume, volume)
            delay(delayPerStep)
        }
    }
}