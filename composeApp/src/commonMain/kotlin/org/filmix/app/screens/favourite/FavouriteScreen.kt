package org.filmix.app.screens.favourite

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import org.filmix.app.components.MoviesSection

object FavouriteScreen : Screen {

    override val key = "FavouriteScreen"

    @Composable
    override fun Content() {
        val model = getScreenModel<FavouriteScreenModel>()

        if (model.preferences.isAuthorized) {
            LazyColumn {
                items(model.sections) {
                    MoviesSection(it.title, it.movies)
                }
            }
        } else {
            Text("Login to see your favourite and saved movies")
        }
    }
}