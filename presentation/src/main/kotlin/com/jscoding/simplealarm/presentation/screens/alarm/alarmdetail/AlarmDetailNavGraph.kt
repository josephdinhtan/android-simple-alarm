package com.jscoding.simplealarm.presentation.screens.alarm.alarmdetail

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.jscoding.simplealarm.presentation.screens.settings.ringtone.RingtonePickerScreen

fun NavGraphBuilder.alarmDetailNavGraph(
    navController: NavHostController,
    route: String = "nav_alarm_edit/{alarm_id}",
) {
    composable(
        route,
        arguments = listOf(navArgument("alarm_id") {
            type = NavType.LongType
            defaultValue = -1
        })
    ) {
        val alarmId = it.arguments?.getLong("alarm_id") ?: -1
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
        arguments = listOf(navArgument("alarm_id") {
            type = NavType.LongType
            defaultValue = -1
        })
    ) {
        val alarmId = it.arguments?.getLong("alarm_id") ?: -1
        RingtonePickerScreen(
            alarmId = alarmId,
            title = "Alarm ringtone",
            onBack = { navController.navigateUp() },
        )
    }
}