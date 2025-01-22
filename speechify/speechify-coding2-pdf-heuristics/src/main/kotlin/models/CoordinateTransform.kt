package org.speechify.models

import kotlinx.serialization.Serializable

/**
 * A 3-D transform describing deformation of the rectangle
 */
@Serializable
data class CoordinateTransform(
    val a: Double,
    val b: Double,
    val c: Double,
    val d: Double,
    val tx: Double,
    val ty: Double,
) {
    fun apply(x: Double, y: Double): Pair<Double, Double> {
        return Pair(a * x + c * y + tx, b * x + d * y + ty)
    }
}