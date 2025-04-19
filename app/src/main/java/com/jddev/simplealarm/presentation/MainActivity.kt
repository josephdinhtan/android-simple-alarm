package com.jddev.simplealarm.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.jddev.simplealarm.presentation.screens.debug.DevControlPanelContent
import com.jddev.simplealarm.presentation.screens.debug.DevUtility
import com.jddev.simpletouch.utils.debugui.DevUtilityUi
import com.jddev.simpletouch.utils.logging.LogManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var logManager: LogManager

    @Inject
    lateinit var devUtility: DevUtility

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DevUtilityUi(
                modifier = Modifier.fillMaxSize(),
                isEnable = true,
                logManager = logManager,
                devControlPanelContent = { DevControlPanelContent(devUtility) }
            ) {
                SimpleAlarmApp()
            }
        }
    }
}