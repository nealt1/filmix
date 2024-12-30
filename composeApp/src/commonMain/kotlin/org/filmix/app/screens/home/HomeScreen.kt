package org.filmix.app.screens.home

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import org.filmix.app.components.MoviesSection
import org.filmix.app.ui.LocalUserInfo

object HomeScreen : Screen {

    override val key = "HomeScreen"

    @Composable
    override fun Content() {
        val user = LocalUserInfo.current
        val model = getScreenModel<HomeScreenModel>()
        val sections = remember { model.sections }
        val recentSection = remember { model.recentSection }

        LazyColumn {
            items(
                items = sections,
                key = { it.title.key },
            ) { section ->
                MoviesSection(section)
            }

            if (user.isAuthorized) {
                item(key = recentSection.title.key) {
                    MoviesSection(recentSection)
                }
            }
        }
    }
}