package org.filmix.app.models

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = MovieSectionSerializer::class)
enum class MovieSection(val id: Int) {
    Movie(999),
    Series(7),
    Cartoon(14),
    CartoonSeries(93),
    Other(0)
}


object MovieSectionSerializer : KSerializer<MovieSection> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Color", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: MovieSection) {
        encoder.encodeInt(value.id)
    }

    override fun deserialize(decoder: Decoder): MovieSection {
        val id = decoder.decodeInt()
        return enumValues<MovieSection>().firstOrNull { it.id == id } ?: MovieSection.Other
    }
}