package org.filmix.app.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.filmix.app.data.StringToFloatNullableSerializer
import org.filmix.app.data.StringToIntNullableSerializer
import org.filmix.app.data.StringToVideoRelatedSerializer

@Serializable
data class VideoDetails(
    override val id: Int,
    override val section: MovieSection,
    override val alt_name: String,
    override val title: String,
    override val original_title: String,
    override val year: Int,
    val year_end: Int,
    val duration: Int,
    override val date: String,
    override val date_atom: Instant,
    override val favorited: Boolean,
    override val watch_later: Boolean,
    override val last_episode: VideoEpisode?,
    override val actors: List<String>,
    val found_actors: List<VideoActor>,
    val directors: List<String>,
    override val poster: String,
    val short_story: String,
    val player_links: PlayerLinks,
    @Serializable(with = StringToFloatNullableSerializer::class)
    val kp_rating: Float?,
    @Serializable(with = StringToIntNullableSerializer::class)
    val kp_votes: Int?,
    @Serializable(with = StringToFloatNullableSerializer::class)
    val imdb_rating: Float?,
    @Serializable(with = StringToIntNullableSerializer::class)
    val imdb_votes: Int?,
    override val serial_stats: VideoSeriesData?,
    val rip: String,
    override val quality: String,
    override val rating: Int,
    override val categories: List<String>,
    val post_url: String,
    override val countries: List<String>,
    @Serializable(with = StringToVideoRelatedSerializer::class)
    val relates: List<VideoRelated>
) : VideoInfo