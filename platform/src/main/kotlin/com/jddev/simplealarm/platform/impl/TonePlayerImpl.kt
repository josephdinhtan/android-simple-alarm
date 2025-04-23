package com.jddev.simplealarm.platform.impl

import android.net.Uri
import com.jddev.simplealarm.platform.helper.MediaPlayerHelper
import com.jscoding.simplealarm.domain.platform.TonePlayer
import javax.inject.Inject

class TonePlayerImpl @Inject constructor(
    private val mediaPlayerHelper: MediaPlayerHelper,
) : TonePlayer {
    override fun play(uri: Uri, volume: Float) {
        mediaPlayerHelper.play(uri, volume)
    }

    override fun stop() {
        mediaPlayerHelper.stop()
    }
}