package org.filmix.app.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonPrimitive
import org.filmix.app.models.VideoRelated

val videoRelatedKSerializer = VideoRelated.serializer()
private val videoListSerializer = ListSerializer(videoRelatedKSerializer)

object StringToVideoRelatedSerializer : KSerializer<List<VideoRelated>> by videoListSerializer {
    override fun deserialize(decoder: Decoder): List<VideoRelated> {
        val jsonDecoder = decoder as JsonDecoder
        val json = jsonDecoder.decodeJsonElement()

        return if (json is JsonArray) {
            decoder.json.decodeFromJsonElement(videoListSerializer, json)
        } else {
            emptyList()
        }
    }
}