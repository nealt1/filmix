package org.filmix.app.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class VideoData(
    override val id: Int,
    override val alt_name: String,
    override val section: MovieSection,
    override val title: String,
    override val original_title: String,
    override val poster: String,
    override val year: Int,
    override val date: String,
    override val date_atom: Instant,
    override val favorited: Boolean,
    override val watch_later: Boolean,
    override val actors: List<String>,
    override val countries: List<String>,
    override val categories: List<String>,
    override val quality: String,
    override val rating: Int,
    override val serial_stats: VideoSeriesData? = null,
    override val last_episode: VideoEpisode? = null,
    val additional: VideoAdditionalData? = null
) : VideoInfo