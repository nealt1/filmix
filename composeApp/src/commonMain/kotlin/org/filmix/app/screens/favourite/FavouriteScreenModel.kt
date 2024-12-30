package org.filmix.app.screens.favourite

import androidx.paging.PagingConfig
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import org.filmix.app.components.createSectionModel
import org.filmix.app.data.VideoRepository

class FavouriteScreenModel(
    private val repository: VideoRepository,
    pagingConfig: PagingConfig
) : ScreenModel {
    val sections = with(screenModelScope) {
        listOf(
            createSectionModel("Favorite", pagingConfig) {
                repository.getFavourite(it)
            },
            createSectionModel("Saved", pagingConfig) {
                repository.getSaved(it)
            }
        )
    }
}