package org.speechify.models

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("type")
sealed class TextBlock {
    abstract val text: String

    @Serializable
    @SerialName("Heading")
    data class Heading(override val text: String) : TextBlock()

    @Serializable
    @SerialName("Paragraph")
    data class Paragraph(override val text: String) : TextBlock()

    @Serializable
    @SerialName("Marginalia")
    data class Marginalia(override val text: String) : TextBlock()
}
