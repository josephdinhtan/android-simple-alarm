package com.jddev.simplealarm.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jddev.simplealarm.presentation.screens.HomeScreen
import com.jddev.simplealarm.presentation.screens.alarm.AddNewAlarmRoute
import com.jddev.simplealarm.presentation.screens.alarm.EditAlarmRoute
import com.jddev.simplealarm.presentation.screens.settings.settingsNavGraph
import com.jddev.simpletouch.ui.foundation.StUiDoubleBackHandler
import com.jddev.simpletouch.ui.navigation.StUiNavHost

@Composable
fun RootNavGraph(
    modifier: Modifier = Modifier,
    rootNavController: NavHostController = rememberNavController(),
) {

    StUiDoubleBackHandler(
        toastMessage = "Press again to exit the app",
    )

    StUiNavHost (
        navController = rootNavController,
        startDestination = "nav_home",
        modifier = modifier,
    ) {
        composable("nav_home") {
            HomeScreen(
                navigateToAlarmEdit = { alarmId -> rootNavController.navigate("nav_alarm_edit/$alarmId") },
                navigateToSettings = { rootNavController.navigate("nav_settings") }
            )
        }
        composable(
            "nav_alarm_edit/{alarmId}",
            arguments = listOf(navArgument("alarmId") {
                type = NavType.LongType
                defaultValue = -1
            })
        ) {
            val alarmId = it.arguments?.getLong("alarmId") ?: -1
            if (alarmId.toInt() == -1) {
                AddNewAlarmRoute(onBack = { rootNavController.navigateUp() })
            } else {
                EditAlarmRoute(
                    alarmId = alarmId,
                    onBack = { rootNavController.navigateUp() }
                )
            }
        }
        settingsNavGraph("nav_settings", rootNavController)
    }
}