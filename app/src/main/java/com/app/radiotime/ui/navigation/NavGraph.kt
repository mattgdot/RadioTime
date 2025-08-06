package com.app.radiotime.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.app.radiotime.ui.MainViewModel
import com.app.radiotime.ui.screens.discover.DiscoverScreen
import com.app.radiotime.ui.screens.favorites.FavoriteScreen
import com.app.radiotime.ui.screens.more.MoreScreen

@Composable
fun NavGraph(navHostController: NavHostController, modifier: Modifier) {
    val mainViewModel: MainViewModel = viewModel()
    val initialRoute =
        if (mainViewModel.hasSaved) NavigationItem.Favorites.route else NavigationItem.Discover.route

    NavHost(navController = navHostController, startDestination = initialRoute) {
        composable(NavigationItem.Favorites.route) {
            FavoriteScreen(mainViewModel)
        }
        composable(NavigationItem.Discover.route) {
            DiscoverScreen(mainViewModel)
        }
        composable(NavigationItem.More.route) {
            MoreScreen(mainViewModel = mainViewModel)
        }
    }
}