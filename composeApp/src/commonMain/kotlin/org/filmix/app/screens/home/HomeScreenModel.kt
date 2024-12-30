package org.filmix.app.screens.home

import androidx.paging.PagingConfig
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import org.filmix.app.components.createSectionModel
import org.filmix.app.data.VideoRepository
import org.filmix.app.models.MovieSection
import org.filmix.app.screens.settings.Preferences

class HomeScreenModel(
    private val repository: VideoRepository,
    private val preferences: Preferences,
    pagingConfig: PagingConfig
) : ScreenModel {

    val sections = with(screenModelScope) {
        listOfNotNull(
            createSectionModel("Trending", pagingConfig) {
                repository.getTrending(it)
            },
            createSectionModel("Popular", pagingConfig) {
                repository.getPopular(it)
            },
            createSectionModel("Movies", pagingConfig) {
                repository.getCatalog(it, MovieSection.Movie)
            },
            createSectionModel("Series", pagingConfig) {
                repository.getCatalog(it, MovieSection.Series)
            },
            createSectionModel("Cartoons", pagingConfig) {
                repository.getCatalog(it, MovieSection.Cartoon)
            },
            createSectionModel("Cartoon Series", pagingConfig) {
                repository.getCatalog(it, MovieSection.CartoonSeries)
            },
            createSectionModel("Recent", pagingConfig) {
                repository.getHistory(it)
            }.takeIf { preferences.isAuthorized }
        )
    }
}