package com.jscoding.simplealarm.domain.entity.alarm

import android.net.Uri

data class Ringtone(
    val title: String,
    val uri: Uri,
) {
    companion object {
        val Silent = Ringtone("Silent", Uri.EMPTY)
    }
}
