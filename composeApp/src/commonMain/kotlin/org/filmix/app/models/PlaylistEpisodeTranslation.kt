package org.filmix.app.models

import kotlinx.serialization.Serializable

@Serializable
data class PlaylistEpisodeTranslation(
    val name: String,
    val episodes: List<PlaylistEpisode>
)