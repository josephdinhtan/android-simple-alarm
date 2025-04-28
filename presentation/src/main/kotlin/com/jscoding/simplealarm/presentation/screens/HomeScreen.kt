package com.jscoding.simplealarm.presentation.screens

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jddev.simpletouch.ui.foundation.topappbar.StUiTopAppBar
import com.jddev.simpletouch.ui.foundation.topappbar.stUiLargeTopAppbarScrollBehavior
import com.jscoding.simplealarm.presentation.components.ClockBottomBar
import com.jscoding.simplealarm.presentation.components.ClockNavItem
import com.jscoding.simplealarm.presentation.screens.alarm.AlarmRoute
import com.jscoding.simplealarm.presentation.screens.alarm.AlarmTopAppBar
import com.jscoding.simplealarm.presentation.screens.clock.ClockScreen
import com.jscoding.simplealarm.presentation.screens.stopwatch.StopwatchScreen
import com.jscoding.simplealarm.presentation.screens.timer.TimerScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeNavController: NavHostController = rememberNavController(),
    navigateToAlarmEdit: (alarmId: Long) -> Unit,
    navigateToSettings: () -> Unit,
) {
    val scrollBehavior = stUiLargeTopAppbarScrollBehavior()
    val navBackStackEntry by homeNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Scaffold(contentWindowInsets = WindowInsets.safeDrawing, topBar = {
        when (currentRoute) {
            ClockNavItem.AlarmNav.route -> AlarmTopAppBar(scrollBehavior = scrollBehavior,
                navigateToSettings = navigateToSettings,
                onAddClick = {
                    navigateToAlarmEdit(-1)
                })

            null -> {}
            else -> {
                StUiTopAppBar(
                    title = currentRoute.toString(),
                )
            }
        }
    }, bottomBar = {
        ClockBottomBar(navController = homeNavController)
    }) { innerPadding ->
        NavHost(
            navController = homeNavController,
            startDestination = ClockNavItem.AlarmNav.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(ClockNavItem.AlarmNav.route) {
                AlarmRoute(
                    scrollBehavior = scrollBehavior,
                    onEditAlarm = {
                        navigateToAlarmEdit(it.id)
                    },
                )
            }
            composable(ClockNavItem.ClockNav.route) { ClockScreen() }
            composable(ClockNavItem.TimerNav.route) { TimerScreen() }
            composable(ClockNavItem.StopwatchNav.route) { StopwatchScreen() }
        }
    }
}