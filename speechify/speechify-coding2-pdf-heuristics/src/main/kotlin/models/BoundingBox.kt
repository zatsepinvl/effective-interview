package org.speechify.models

import kotlinx.serialization.Serializable

/**
 * A representation of a rectangle in an image coordinate space, possibly transformed and translated from the origin
 */
@Serializable
data class BoundingBox(
    /**
     * The width of the box in coordinate space prior to application of the transform
     */
    val width: Double,

    /**
     * The height of the box in coordinate space prior to application of the transform
     */
    val height: Double,

    /**
     * A 3-D transform describing deformation of the rectangle
     */
    val transform: CoordinateTransform,
) {
    /**
     * The x-coordinate of the top-left corner of the box, after the transformation is applied
     */
    val left: Double get() = transform.apply(0.0, 0.0).first

    /**
     * The x-coordinate of the top-right corner of the box, after the transformation is applied
     */
    val right: Double get() = left + width

    /**
     * The y-coordinate of the top-left corner of the box, after the transformation is applied
     */
    val top: Double get() = transform.apply(0.0, 0.0).second

    /**
     * The y-coordinate of the bottom-left corner of the box, after the transformation is applied
     */
    val bottom: Double get() = top + height

    /**
     * The y-coordinate of the center of the box, after the transformation is applied.
     */
    val centerY: Double get() = top + height / 2

    /**
     * The x-coordinate of the center of the box, after the transformation is applied.
     */
    val centerX: Double get() = left + width / 2
}