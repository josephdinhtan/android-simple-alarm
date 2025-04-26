package com.jscoding.simplealarm.presentation.screens.alarm.alarmdetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument

fun NavGraphBuilder.alarmDetailNavGraph(
    route: String = "nav_alarm_detail_graph/{alarm_id}",
    navController: NavHostController,
) {
    navigation(
        route = route,
        startDestination = "nav_detail_alarm_home",
        arguments = listOf(
            navArgument("alarm_id") {
                type = NavType.LongType
            }
        )
    ) {
        composable("nav_detail_alarm_home") {
            val viewModel = it.sharedAlarmDetailViewModel<AlarmDetailViewModel>(navController)
            DetailAlarmRoute(
                viewModel = viewModel,
                navigateToRingtone = {
                    navController.navigate("nav_detail_alarm_ringtone")
                },
                onBack = { navController.navigateUp() }
            )
        }

        composable("nav_detail_alarm_ringtone") {
            val viewModel = it.sharedAlarmDetailViewModel<AlarmDetailViewModel>(navController)
            LaunchedEffect(Unit) {
                viewModel.setupRingtoneScreen()
            }
            AlarmRingtonePickerRoute(
                viewModel = viewModel,
                onBack = { navController.navigateUp() },
            )
        }
    }
}

@Composable
private inline fun <reified T : ViewModel> NavBackStackEntry.sharedAlarmDetailViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(parentEntry)
}