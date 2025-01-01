package org.filmix.app.screens.favourite

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import org.filmix.app.composeapp.generated.resources.*
import org.filmix.app.components.MoviesSection
import org.filmix.app.components.TextCenter
import org.filmix.app.ui.LocalUserInfo
import org.jetbrains.compose.resources.stringResource

object FavouriteScreen : Screen {

    override val key = "FavouriteScreen"

    @Composable
    override fun Content() {
        val model = getScreenModel<FavouriteScreenModel>()
        val user = LocalUserInfo.current

        if (user.isAuthorized) {
            LazyColumn {
                items(
                    items = model.sections,
                    key = { it.title.key }
                ) {
                    MoviesSection(it)
                }
            }
        } else {
            TextCenter(stringResource(Res.string.favourite_empty))
        }
    }
}