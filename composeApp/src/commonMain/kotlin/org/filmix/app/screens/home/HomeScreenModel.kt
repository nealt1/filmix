package org.filmix.app.screens.home

import androidx.paging.PagingConfig
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import org.filmix.app.composeapp.generated.resources.*
import org.filmix.app.components.createSectionModel
import org.filmix.app.data.VideoRepository
import org.filmix.app.models.MovieSection

class HomeScreenModel(
    private val repository: VideoRepository,
    pagingConfig: PagingConfig
) : ScreenModel {

    val sections = with(screenModelScope) {
        listOfNotNull(
            createSectionModel(Res.string.category_trending, pagingConfig) {
                repository.getTrending(it)
            },
            createSectionModel(Res.string.category_popular, pagingConfig) {
                repository.getPopular(it)
            },
            createSectionModel(Res.string.category_movies, pagingConfig) {
                repository.getCatalog(it, MovieSection.Movie)
            },
            createSectionModel(Res.string.category_series, pagingConfig) {
                repository.getCatalog(it, MovieSection.Series)
            },
            createSectionModel(Res.string.category_cartoons, pagingConfig) {
                repository.getCatalog(it, MovieSection.Cartoon)
            },
            createSectionModel(Res.string.category_cartoons_series, pagingConfig) {
                repository.getCatalog(it, MovieSection.CartoonSeries)
            }
        )
    }

    val recentSection = screenModelScope.createSectionModel(Res.string.category_recent, pagingConfig) {
        repository.getHistory(it)
    }
}