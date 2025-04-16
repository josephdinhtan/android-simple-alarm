package com.jddev.simplealarm.domain.model.alarm

import android.net.Uri

data class AlarmTone(
    val title: String,
    val uri: Uri,
) {
    companion object {
        val Silent = AlarmTone("Silent", Uri.EMPTY)
    }
}
