package com.jscoding.simplealarm.domain.platform

import android.net.Uri

interface TonePlayer {
    fun play(uri: Uri, volume: Float)
    fun stop()
}