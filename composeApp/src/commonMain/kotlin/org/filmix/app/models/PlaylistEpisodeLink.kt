package org.filmix.app.models

import kotlinx.serialization.Serializable

@Serializable
data class PlaylistEpisodeLink(
    val link: String,
    val qualities: List<Int>,
    val watched: Boolean = false
)