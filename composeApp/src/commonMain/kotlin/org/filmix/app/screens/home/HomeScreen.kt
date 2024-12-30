package org.filmix.app.screens.home

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemContentType
import app.cash.paging.compose.itemKey
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import org.filmix.app.components.MoviesSection

object HomeScreen : Screen {

    override val key = "HomeScreen"

    @Composable
    override fun Content() {
        val model = getScreenModel<HomeScreenModel>()
        val sections = remember { model.sections }

        LazyColumn {
            items(sections,
                key = { it.title },
            ) { section ->
                MoviesSection(section.title, section.movies)
            }
        }
    }
}