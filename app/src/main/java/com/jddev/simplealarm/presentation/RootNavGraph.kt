package com.jddev.simplealarm.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jddev.simplealarm.presentation.screens.HomeScreen
import com.jddev.simplealarm.presentation.screens.alarm.alarmdetail.AddNewAlarmRoute
import com.jddev.simplealarm.presentation.screens.alarm.alarmdetail.EditAlarmRoute
import com.jddev.simplealarm.presentation.screens.alarm.alarmdetail.alarmDetailNavGraph
import com.jddev.simplealarm.presentation.screens.settings.settingsNavGraph
import com.jddev.simpletouch.ui.foundation.StUiDoubleBackHandler
import com.jddev.simpletouch.ui.navigation.StUiNavHost

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
                navigateToAlarmEdit = { alarmId -> rootNavController.navigate("nav_alarm_edit/$alarmId") },
                navigateToSettings = { rootNavController.navigate("nav_settings") }
            )
        }
        alarmDetailNavGraph(navController = rootNavController, route = "nav_alarm_edit/{alarmId}")
        settingsNavGraph("nav_settings", rootNavController)
    }
}