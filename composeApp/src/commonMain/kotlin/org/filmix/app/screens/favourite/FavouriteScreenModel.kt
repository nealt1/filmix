package org.filmix.app.screens.favourite

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import org.filmix.app.data.VideoRepository
import org.filmix.app.paging.IntPagingSource

class FavouriteScreenModel(
    private val repository: VideoRepository,
    pagingConfig: PagingConfig
) : ScreenModel {
    val favouriteMovies = Pager(pagingConfig) {
        IntPagingSource(repository::getFavourite)
    }.flow.cachedIn(screenModelScope)

    val savedMovies = Pager(pagingConfig) {
        IntPagingSource(repository::getSaved)
    }.flow.cachedIn(screenModelScope)
}