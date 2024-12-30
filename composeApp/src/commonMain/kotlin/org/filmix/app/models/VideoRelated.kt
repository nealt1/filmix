package org.filmix.app.models

import kotlinx.serialization.Serializable

@Serializable
data class VideoRelated(
    override val id: Int,
    override val title: String,
    override val poster: String,
    override val year: Int,
    override val alt_name: String
) : Video