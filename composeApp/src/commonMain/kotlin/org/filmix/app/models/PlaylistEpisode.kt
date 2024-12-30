package org.filmix.app.models

import kotlinx.serialization.Serializable

@Serializable
data class PlaylistEpisode(
    val name: String,
    val link: PlaylistEpisodeLink
)
