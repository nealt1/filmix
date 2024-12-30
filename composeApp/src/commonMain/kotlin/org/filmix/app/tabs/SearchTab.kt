package org.filmix.app.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition
import org.filmix.app.screens.search.SearchScreen

object SearchTab : Tab {

    override val key = "SearchTab"

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Search)

            return remember {
                TabOptions(
                    index = 1u,
                    title = "Search",
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(SearchScreen, key = key) { navigator ->
            SlideTransition(navigator)
        }
    }
}