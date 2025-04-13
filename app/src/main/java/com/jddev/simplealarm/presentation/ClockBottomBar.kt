package com.jddev.simplealarm.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.HourglassTop
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun ClockBottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    NavigationBar {
        ClockNavItem.entries.forEach { item ->
            val selected = currentDestination?.route == item.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    if (selected) {
                        Icon(
                            imageVector = item.selectedIcon,
                            contentDescription = item.label
                        )
                    } else {
                        Icon(
                            imageVector = item.unselectedIcon,
                            contentDescription = item.label
                        )
                    }
                },
                label = {
                    Text(text = item.label)
                },
                alwaysShowLabel = true
            )
        }
    }
}

internal enum class ClockNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    AlarmNav ("nav_alarm", "Alarm", Icons.Filled.Alarm, Icons.Outlined.Alarm),
    ClockNav ("nav_clock", "Clock", Icons.Filled.Schedule, Icons.Outlined.Schedule),
    TimerNav ("nav_timer", "Timer", Icons.Filled.HourglassBottom, Icons.Outlined.HourglassTop),
    StopwatchNav ("nav_stopwatch", "Stopwatch", Icons.Filled.Timer, Icons.Outlined.Timer);
}