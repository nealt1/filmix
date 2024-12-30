package org.filmix.app.models

import kotlinx.serialization.Serializable

@Serializable
data class VideoEpisode(
    val season: String = "",
    val episode: String = "",
    val translation: String = "",
    val date: String = ""
)