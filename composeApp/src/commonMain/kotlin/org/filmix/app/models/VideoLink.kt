package org.filmix.app.models

import kotlinx.serialization.Serializable

@Serializable
data class VideoLink(
    val link: String,
    val translation: String
)