package org.filmix.app.screens.video

sealed class Playlist {
    abstract val trailers: List<Translation>

    data class Series(
        override val trailers: List<Translation>,
        val seasons: List<Season>
    ) : Playlist()

    data class Movie(
        override val trailers: List<Translation>,
        val translations: List<Translation>
    ) : Playlist()
}

data class Season(
    val name: String,
    val episodes: List<Episode>
)

data class Episode(
    val name: String,
    val translations: List<Translation>
)

data class Translation(
    val name: String,
    val link: VideoLink
)

data class VideoLink(
    val url: String,
    val quality: List<Int>
)