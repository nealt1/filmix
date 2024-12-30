package org.filmix.app.models

import kotlinx.serialization.Serializable

@Serializable
data class PlayerLinks(
    val movie: List<VideoLink>,
    val playlist: Playlist,
    val trailer: List<VideoLink>
)