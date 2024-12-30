package org.filmix.app.models

import kotlinx.serialization.Serializable

@Serializable
data class VideoActor(
    val id: Int,
    val name: String,
    val original_name: String
)