package org.speechify.models

import kotlinx.serialization.Serializable

@Serializable
data class WordWithBoundingBox(
    val text: String,
    /**
     * The coordinates of this are normalized between 0 and 1, where:
     * - 0 represents the left or bottom edge of the page.
     * - 1 represents the top or right edge of the page.
     */
    val boundingBox: BoundingBox,
)
