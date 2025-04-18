package com.jddev.simplealarm.presentation.screens.alarm.alarmdetail

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.jddev.simplealarm.presentation.screens.settings.ringtone.RingtonePickerScreen

fun NavGraphBuilder.alarmDetailNavGraph(
    navController: NavHostController,
    route: String = "nav_alarm_edit/{alarmId}",
) {
    composable(
        route,
        arguments = listOf(navArgument("alarmId") {
            type = NavType.LongType
            defaultValue = -1
        })
    ) {
        val alarmId = it.arguments?.getLong("alarmId") ?: -1
        if (alarmId.toInt() == -1) {
            AddNewAlarmRoute(
                navigateToRingtone = { navController.navigate("nav_detail_alarm_ringtone/$alarmId") },
                onBack = { navController.navigateUp() }
            )
        } else {
            EditAlarmRoute(
                alarmId = alarmId,
                navigateToRingtone = { navController.navigate("nav_detail_alarm_ringtone/$alarmId") },
                onBack = { navController.navigateUp() }
            )
        }
    }

    composable(
        "nav_detail_alarm_ringtone/{alarmId}",
        arguments = listOf(navArgument("alarmId") {
            type = NavType.LongType
            defaultValue = -1
        })
    ) {
        val alarmId = it.arguments?.getLong("alarmId") ?: -1
        RingtonePickerScreen(
            alarmId = alarmId,
            title = "Alarm ringtone",
            onBack = { navController.navigateUp() },
        )
    }
}