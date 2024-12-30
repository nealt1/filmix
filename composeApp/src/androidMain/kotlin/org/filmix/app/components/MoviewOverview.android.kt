package org.filmix.app.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.datetime.Clock
import org.filmix.app.models.MovieSection
import org.filmix.app.models.VideoAdditionalData
import org.filmix.app.models.VideoData
import org.filmix.app.screens.home.HomeScreen
import org.filmix.app.tabs.HomeTab

@Preview
@Composable
fun MovieOverviewPreview() {
    Navigator(HomeScreen, key = HomeTab.key) { navigator ->
        MovieOverview(
            VideoData(
                id = 123,
                alt_name = "Alt name",
                section = MovieSection.Movie,
                title = "Very long movie title long movie title",
                original_title = "Original Title",
                poster = "https://thumbs.filmixapp.cyou/posters/1221/thumbs/w220/italyanskaya-rabota-the-italian-job-1969_32670_0.jpg",
                year = 2020,
                date = "2024-03-03",
                date_atom = Clock.System.now(),
                favorited = true,
                watch_later = true,
                actors = emptyList(),
                countries = emptyList(),
                categories = emptyList(),
                quality = "HD",
                rating = 123,
                additional = VideoAdditionalData(
                    watch_date = "2024-03-03",
                    translation = "Translation",
                    season = null,
                    episode = null
                )
            )
        )
    }
}