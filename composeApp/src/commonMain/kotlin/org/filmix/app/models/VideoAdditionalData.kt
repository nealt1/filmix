package org.filmix.app.models

import kotlinx.serialization.Serializable

@Serializable
data class VideoAdditionalData(
    val watch_date: String,
    val translation: String,
    val season: Int?,
    val episode: Int?
)