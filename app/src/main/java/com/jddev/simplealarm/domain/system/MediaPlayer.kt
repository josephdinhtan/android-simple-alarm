package com.jddev.simplealarm.domain.system

import android.net.Uri

interface MediaPlayer {
    fun play(uri: Uri, volume: Float)
    fun stop()
}