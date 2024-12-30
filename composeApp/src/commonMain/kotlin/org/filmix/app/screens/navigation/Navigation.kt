package org.filmix.app.screens.navigation

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import org.filmix.app.tabs.FavouriteTab
import org.filmix.app.tabs.HomeTab
import org.filmix.app.tabs.SearchTab
import org.filmix.app.tabs.SettingsTab
import org.filmix.app.ui.LocalWindowSizeClass
import org.filmix.app.ui.NavigationBarState

@Composable
fun BottomNavigationBar() {
    val windowSizeClass = LocalWindowSizeClass.current
    val isNavigationBarVisible by NavigationBarState.isVisible

    if (
        isNavigationBarVisible &&
        windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact &&
        windowSizeClass.heightSizeClass != WindowHeightSizeClass.Compact
    ) {
        BottomNavigation(
            backgroundColor = MaterialTheme.colorScheme.surfaceContainer
        ) {
            TabNavigationItem(HomeTab)
            TabNavigationItem(FavouriteTab)
            TabNavigationItem(SearchTab)
            TabNavigationItem(SettingsTab)
        }
    }
}

@Composable
fun SideNavigationBar() {
    val windowSizeClass = LocalWindowSizeClass.current
    val isNavigationBarVisible by NavigationBarState.isVisible

    if (
        isNavigationBarVisible &&
        windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact
    ) {
        NavigationRail(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Spacer(Modifier.weight(1f))

            TabNavigationItem(HomeTab)
            TabNavigationItem(FavouriteTab)
            TabNavigationItem(SearchTab)

            Spacer(Modifier.weight(1f))

            TabNavigationItem(SettingsTab)
        }
    }
}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current

    BottomNavigationItem(
        selected = tabNavigator.current.key == tab.key,
        onClick = { tabNavigator.current = tab },
        icon = {
            Icon(
                painter = tab.options.icon!!,
                contentDescription = tab.options.title,
            )
        }
    )
}

@Composable
private fun ColumnScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current

    NavigationRailItem(
        selected = tabNavigator.current.key == tab.key,
        onClick = { tabNavigator.current = tab },
        icon = {
            Icon(
                painter = tab.options.icon!!,
                contentDescription = tab.options.title,
            )
        }
    )
}