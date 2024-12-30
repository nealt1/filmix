package org.filmix.app.models

import kotlinx.serialization.Serializable

@Serializable
data class PlaylistSeason(
    val name: String,
    val translations: List<PlaylistEpisodeTranslation>
)