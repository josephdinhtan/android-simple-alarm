package com.jscoding.simplealarm.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jscoding.simplealarm.presentation.screens.HomeScreen
import com.jscoding.simplealarm.presentation.screens.alarm.alarmdetail.alarmDetailNavGraph
import com.jscoding.simplealarm.presentation.screens.settings.settingsNavGraph
import com.jddev.simpletouch.ui.foundation.StUiDoubleBackHandler
import com.jddev.simpletouch.ui.navigation.StUiNavHost
import com.jddev.simpletouch.ui.navigation.navigateSingleTop

@Composable
fun RootNavGraph(
    rootNavController: NavHostController = rememberNavController(),
) {

    StUiDoubleBackHandler(
        toastMessage = "Press again to exit the app",
    )

    StUiNavHost(
        navController = rootNavController,
        startDestination = "nav_home",
    ) {
        composable("nav_home") {
            HomeScreen(
                navigateToAlarmEdit = { alarmId ->
                    rootNavController.navigateSingleTop("nav_alarm_detail_graph/$alarmId")
                },
                navigateToSettings = { rootNavController.navigate("nav_settings") }
            )
        }
        alarmDetailNavGraph("nav_alarm_detail_graph/{alarm_id}", rootNavController)
        settingsNavGraph("nav_settings", rootNavController)
    }
}