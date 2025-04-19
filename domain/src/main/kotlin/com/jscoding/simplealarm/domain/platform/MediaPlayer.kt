package com.jscoding.simplealarm.domain.platform

import android.net.Uri

interface MediaPlayer {
    fun play(uri: Uri, volume: Float)
    fun stop()
}