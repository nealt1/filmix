package org.filmix.app.screens.home

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import org.filmix.app.data.VideoRepository
import org.filmix.app.models.MovieSection
import org.filmix.app.paging.IntPagingSource

class HomeScreenModel(
    private val repository: VideoRepository,
    pagingConfig: PagingConfig
) : ScreenModel {
    val recentMovies = Pager(pagingConfig) {
        IntPagingSource(repository::getHistory)
    }.flow.cachedIn(screenModelScope)

    val popularMovies = Pager(pagingConfig) {
        IntPagingSource(repository::getPopular)
    }.flow.cachedIn(screenModelScope)

    val trendingMovies = Pager(pagingConfig) {
        IntPagingSource(repository::getTrending)
    }.flow.cachedIn(screenModelScope)

    val allMovies = Pager(pagingConfig) {
        IntPagingSource { page ->
            repository.getCatalog(page, MovieSection.Movie)
        }
    }.flow.cachedIn(screenModelScope)

    val allSeries = Pager(pagingConfig) {
        IntPagingSource { page ->
            repository.getCatalog(page, MovieSection.Series)
        }
    }.flow.cachedIn(screenModelScope)

    val allCartoons = Pager(pagingConfig) {
        IntPagingSource { page ->
            repository.getCatalog(page, MovieSection.Cartoon)
        }
    }.flow.cachedIn(screenModelScope)

    val allCartoonSeries = Pager(pagingConfig) {
        IntPagingSource { page ->
            repository.getCatalog(page, MovieSection.CartoonSeries)
        }
    }.flow.cachedIn(screenModelScope)
}