package com.example.billiardgame.util

import androidx.compose.ui.geometry.Offset
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt

object MathUtils {
    const val TWO_PI = 2.0 * PI

    fun add(a: Offset, b: Offset) = Offset(a.x + b.x, a.y + b.y)
    fun sub(a: Offset, b: Offset) = Offset(a.x - b.x, a.y - b.y)
    fun mul(v: Offset, s: Float) = Offset(v.x * s, v.y * s)
    fun dot(a: Offset, b: Offset) = a.x * b.x + a.y * b.y
    fun magnitude(v: Offset) = sqrt(dot(v, v))
    fun normalize(v: Offset): Offset {
        val m = magnitude(v)
        return if (m > 0) Offset(v.x / m, v.y / m) else Offset.Zero
    }
    fun clamp(v: Float, min: Float, max: Float) = max(min, min(max, v))
    fun lerp(a: Float, b: Float, t: Float) = a + (b - a) * t
    fun angleBetween(from: Offset, to: Offset) = atan2(to.y - from.y, to.x - from.x)

    fun rotateAround(point: Offset, center: Offset, angleDegrees: Float): Offset {
        val angle = angleDegrees * PI / 180f
        val cos = cos(angle.toDouble())
        val sin = sin(angle.toDouble())
        val dx = point.x - center.x
        val dy = point.y - center.y
        return Offset(
            center.x + (dx * cos - dy * sin).toFloat(),
            center.y + (dx * sin + dy * cos).toFloat()
        )
    }

    fun distance(a: Offset, b: Offset) = magnitude(sub(a, b))

    /** Reflect a direction off a wall normal */
    fun reflect(direction: Offset, normal: Offset) = direction - 2 * dot(direction, normal) * normal
}
