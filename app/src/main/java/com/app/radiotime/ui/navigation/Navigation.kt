package com.app.radiotime.ui.navigation

import com.app.radiotime.R

enum class Screen(val routeName:String) {
    FAVORITES("Favorites"),
    DISCOVER("Discover"),
    MORE("More"),
}

sealed class NavigationItem(val route: String, val icon: Int, val iconFilled: Int, val routeName:String) {
    data object Favorites: NavigationItem(Screen.FAVORITES.name, R.drawable.ic_favorite_outlined, R.drawable.ic_favorite_filled,Screen.FAVORITES.routeName)
    data object Discover: NavigationItem(Screen.DISCOVER.name, R.drawable.ic_radio_outlined, R.drawable.ic_radio_filled,Screen.DISCOVER.routeName)
    data object More: NavigationItem(Screen.MORE.name, R.drawable.ic_more_horizontal, R.drawable.ic_more_horizontal,Screen.MORE.routeName)
}