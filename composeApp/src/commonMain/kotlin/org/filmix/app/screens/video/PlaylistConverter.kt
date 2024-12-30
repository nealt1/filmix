package org.filmix.app.screens.video

import org.filmix.app.models.PlayerLinks
import org.filmix.app.models.PlaylistSeason

object PlaylistConverter {
    fun getPlaylist(playlist: PlayerLinks): Playlist {
        val trailers = playlist.trailer.map {
            Translation(
                name = it.translation,
                link = getVideoLink(it.link)
            )
        }

        return if (playlist.movie.isNotEmpty()) {
            getMoviePlaylist(trailers, playlist)
        } else if (playlist.playlist.seasons.isNotEmpty()) {
            getSeriesPlaylist(trailers, playlist)
        } else {
            Playlist.Empty
        }
    }

    private fun getMoviePlaylist(trailers: List<Translation>, playlist: PlayerLinks): Playlist {
        return Playlist.Movie(
            trailers = trailers,
            translations = playlist.movie.map {
                Translation(
                    name = it.translation,
                    link = getVideoLink(it.link)
                )
            }
        )
    }

    private fun getSeriesPlaylist(trailers: List<Translation>, playlist: PlayerLinks): Playlist {
        val seasons = playlist.playlist.seasons.map { season ->
            val episodes = getEpisodes(season)
            Season(
                name = season.name,
                episodes = episodes.map { (name, translations) ->
                    Episode(
                        name = name,
                        translations = translations
                    )
                }
            )
        }

        return Playlist.Series(
            trailers = trailers,
            seasons = seasons
        )
    }

    private fun getEpisodes(season: PlaylistSeason): Map<String, List<Translation>> {
        val episodes = mutableMapOf<String, MutableList<Translation>>()
        season.translations.forEach { translation ->
            translation.episodes.forEach { episode ->
                val translations = episodes.getOrPut(episode.name) { mutableListOf() }
                translations.add(
                    Translation(
                        name = translation.name,
                        link = VideoLink(
                            url = episode.link.link,
                            quality = episode.link.qualities.sortedDescending()
                        )
                    )
                )
            }
        }
        return episodes
    }

    private fun getVideoLink(link: String): VideoLink {
        return qualityRegex.matchEntire(link)?.let { result ->
            val (prefix, quality, suffix) = result.destructured

            val segments = quality.splitToSequence(',')
                .filter { it.isNotEmpty() }
                .mapNotNull { it.toIntOrNull() }
                .sortedDescending()
                .toList()

            VideoLink(
                url = "$prefix%s$suffix",
                quality = segments
            )
        } ?: VideoLink(url = link, quality = emptyList())
    }

    private val qualityRegex = Regex("(?<prefix>[^\\[]+)\\[(?<quality>[^\\]]+)\\](?<suffix>.+)")
}