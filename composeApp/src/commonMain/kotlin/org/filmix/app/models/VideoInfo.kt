package org.filmix.app.models

import kotlinx.datetime.Instant

interface VideoInfo: Video {
    val section: MovieSection
    val original_title: String
    val date: String
    val date_atom: Instant
    val favorited: Boolean
    val watch_later: Boolean
    val actors: List<String>
    val countries: List<String>
    val categories: List<String>
    val quality: String
    val rating: Int
    val serial_stats: VideoSeriesData?
    val last_episode: VideoEpisode?
}

