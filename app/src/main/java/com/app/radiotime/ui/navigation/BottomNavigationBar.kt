package com.app.radiotime.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import java.util.Locale

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        val items = listOf(
            NavigationItem.Favorites, NavigationItem.Discover, NavigationItem.More
        )

        items.forEach { screen ->
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                label = {
                    Text(screen.route.lowercase().replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                    })
                },
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(painter = painterResource(id = if (currentDestination?.hierarchy?.any { it.route == screen.route } == true) screen.iconFilled else screen.icon),
                        contentDescription = screen.route)
                },
            )
        }
    }
}