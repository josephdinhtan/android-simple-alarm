package com.jscoding.simplealarm.data.platform

import android.net.Uri
import com.jscoding.simplealarm.data.helper.MediaPlayerHelper
import com.jscoding.simplealarm.domain.platform.MediaPlayer
import javax.inject.Inject

class MediaPlayerImpl @Inject constructor(
    private val mediaPlayerHelper: MediaPlayerHelper,
) : MediaPlayer {
    override fun play(uri: Uri, volume: Float) {
        mediaPlayerHelper.play(uri, volume)
    }

    override fun stop() {
        mediaPlayerHelper.stop()
    }
}