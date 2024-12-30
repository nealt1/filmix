package org.filmix.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabDisposable
import cafe.adriel.voyager.navigator.tab.TabNavigator
import org.filmix.app.app.AppState
import org.filmix.app.app.AppTheme
import org.filmix.app.components.LoadingIndicator
import org.filmix.app.screens.navigation.BottomNavigationBar
import org.filmix.app.screens.navigation.SideNavigationBar
import org.filmix.app.tabs.FavouriteTab
import org.filmix.app.tabs.HomeTab
import org.filmix.app.tabs.SearchTab
import org.filmix.app.tabs.SettingsTab
import org.filmix.app.ui.ProvideUserInfo
import org.filmix.app.ui.ProvideWindowSizeClass
import org.koin.compose.getKoin

@Composable
fun App() {
    val koin = getKoin()
    val state = remember { koin.get<AppState>() }
    val userProfile by state.userProfile.collectAsState()
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val colorScheme = when (state.theme) {
        AppTheme.AUTO -> if (isSystemInDarkTheme) {
            darkColorScheme()
        } else lightColorScheme()

        AppTheme.DARK -> darkColorScheme()
        AppTheme.LIGHT -> lightColorScheme()
    }

    MaterialTheme(colorScheme) {
        ProvideWindowSizeClass {
            LoadingIndicator(userProfile) {
                ProvideUserInfo(this) {
                    TabNavigator(
                        HomeTab,
                        key = "App",
                        tabDisposable = {
                            TabDisposable(
                                navigator = it,
                                tabs = listOf(HomeTab, FavouriteTab, SearchTab, SettingsTab)
                            )
                        }
                    ) {
                        AppScaffold()
                    }
                }
            }
        }
    }
}

@Composable
private fun AppScaffold() {
    Scaffold(
        bottomBar = {
            BottomNavigationBar()
        },
        content = { paddingValues ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                SideNavigationBar()
                CurrentTab()
            }
        },
    )
}
