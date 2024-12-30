package org.filmix.app.screens.favourite

import androidx.paging.PagingConfig
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import filmix.composeapp.generated.resources.*
import org.filmix.app.components.createSectionModel
import org.filmix.app.data.VideoRepository

class FavouriteScreenModel(
    private val repository: VideoRepository,
    pagingConfig: PagingConfig
) : ScreenModel {
    val sections = with(screenModelScope) {
        listOf(
            createSectionModel(Res.string.category_favorite, pagingConfig) {
                repository.getFavourite(it)
            },
            createSectionModel(Res.string.category_saved, pagingConfig) {
                repository.getSaved(it)
            }
        )
    }
}