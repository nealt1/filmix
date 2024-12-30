package org.filmix.app.models

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject

@Serializable(with = PlaylistSerializer::class)
data class Playlist(
    val seasons: List<PlaylistSeason>
)

object PlaylistSerializer : KSerializer<Playlist> {
    override val descriptor = PrimitiveSerialDescriptor("PlaylistSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Playlist) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Playlist {
        val jsonDecoder = decoder as JsonDecoder
        val json = jsonDecoder.decodeJsonElement()

        val seasons = if (json is JsonObject) {
            getPlaylistSeasons(json)
        } else {
            emptyList()
        }

        return Playlist(seasons)
    }

    private fun getPlaylistSeasons(json: JsonObject) =
        json.entries.map { (name, translations) ->
            PlaylistSeason(
                name = name,
                translations = getEpisodeTranslations(translations as JsonObject)
            )
        }

    private fun getEpisodeTranslations(json: JsonObject) =
        json.entries.map { (name, episodes) ->
            val episodesJson = if (episodes is JsonArray) {
                buildJsonObject {
                    episodes.forEachIndexed { i, episode ->
                        // Some series has 0 element as season trailer
                        if (i > 0) put(i.toString(), episode)
                    }
                }
            } else episodes as JsonObject

            PlaylistEpisodeTranslation(
                name = name,
                episodes = getEpisodes(episodesJson)
            )
        }

    private fun getEpisodes(json: JsonObject) =
        json.entries.map { (name, link) ->
            val episodeLink = Json.decodeFromJsonElement(PlaylistEpisodeLink.serializer(), link)
            PlaylistEpisode(
                name = name,
                link = episodeLink
            )
        }
}