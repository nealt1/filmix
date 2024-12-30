package org.filmix.app.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.jsonPrimitive

object StringToIntSerializer : KSerializer<Int> {
    override val descriptor =
        PrimitiveSerialDescriptor("StringToIntSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Int) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Int {
        val jsonDecoder = decoder as JsonDecoder
        val json = jsonDecoder.decodeJsonElement()
        return json.jsonPrimitive.content.toInt()
    }
}