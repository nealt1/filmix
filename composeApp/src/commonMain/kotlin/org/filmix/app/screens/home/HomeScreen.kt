package org.filmix.app.screens.home

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import org.filmix.app.components.MoviesSection

object HomeScreen : Screen {

    override val key = "HomeScreen"

    @Composable
    override fun Content() {
        val model = getScreenModel<HomeScreenModel>()

        LazyColumn {
            item {
                MoviesSection("Trending", model.trendingMovies)
            }
            item {
                MoviesSection("Popular", model.popularMovies)
            }
            item {
                MoviesSection("Recent", model.recentMovies)
            }
            item {
                MoviesSection("Movies", model.allMovies)
            }
            item {
                MoviesSection("Series", model.allSeries)
            }
            item {
                MoviesSection("Cartoons", model.allCartoons)
            }
            item {
                MoviesSection("Cartoon Series", model.allCartoonSeries)
            }
        }
    }
}