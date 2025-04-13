package com.jddev.simplealarm.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.jddev.simpletouch.ui.theme.StUiTheme

@Composable
fun SimpleAlarmApp() {
    StUiTheme {
        val navController = rememberNavController()
        RootNavGraph(
            rootNavController = navController
        )
    }
}
