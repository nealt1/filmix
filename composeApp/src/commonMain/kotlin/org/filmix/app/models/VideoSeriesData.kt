package org.filmix.app.models

import kotlinx.serialization.Serializable
import org.filmix.app.data.StringToIntSerializer

@Serializable
data class VideoSeriesData(
    @Serializable(with = StringToIntSerializer::class)
    val status: Int,
    val comment: String,
    val status_text: String
)
