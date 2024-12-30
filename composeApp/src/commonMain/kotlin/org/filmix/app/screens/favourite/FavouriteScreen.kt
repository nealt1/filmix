package org.filmix.app.screens.favourite

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import org.filmix.app.components.MoviesSection

object FavouriteScreen : Screen {

    override val key = "FavouriteScreen"

    @Composable
    override fun Content() {
        val model = getScreenModel<FavouriteScreenModel>()

        LazyColumn {
            item {
                MoviesSection("Favorite", model.favouriteMovies)
            }
            item {
                MoviesSection("Saved", model.savedMovies)
            }
        }
    }
}