package com.example.billiardgame.domain.usecase

import androidx.compose.ui.geometry.Offset
import com.example.billiardgame.util.Constants.*
import com.example.billiardgame.util.MathUtils.*
import kotlin.math.PI
import kotlin.math.sqrt
import kotlin.random.Random

object CalculateRackPositions {

    /** Generate ball positions for a triangle rack with randomization. Returns list of 15 balls + cue ball pos. */
    fun generate(tableWidth: Float, tableHeight: Float): RackConfig {
        val random = Random
        // Foot spot is at ~3/4 of table height
        val footX = tableWidth / 2
        val footY = tableHeight * 0.75f

        // Triangle pointing upward (toward top of screen)
        val spacing = BALL_RADIUS * 2.1f  // slight gap between balls
        // Randomize spacing by ±5%
        val spacingVariation = spacing * (1f + random.nextFloat() * 0.1f - 0.05f)

        // Random rotation of the entire rack (keep it upright-ish: -15 to +15 degrees)
        val rotationAngle = random.nextFloat() * 30f - 15f

        // Generate rows: 1+2+3+4+5 = 15 balls
        val ballPositions = mutableListOf<Offset>()
        var rowOffsetX = 0f
        var rowOffsetY = 0f

        for (row in 0 until 5) {
            for (col in 0 until row + 1) {
                var x = footX - row * spacingVariation * sqrt(3.0) / 2f + col * spacingVariation
                var y = footY - row * spacingVariation - rowOffsetY

                // Add tiny jitter to each position
                x += random.nextFloat() * 4f - 2f
                y += random.nextFloat() * 4f - 2f

                ballPositions.add(Offset(x, y))
            }
        }

        // Rotate all positions around foot center
        val rotated = ballPositions.map { p ->
            rotateAround(p, Offset(footX, footY), rotationAngle)
        }

        // Cue ball position: head string area (1/4 from top), randomly offset in X
        val cueBallY = tableHeight * 0.22f
        val cueBallX = tableWidth / 2 + random.nextFloat() * 100f - 50f
        val cueBallPos = Offset(clamp(cueBallX, RAIL_WIDTH + BALL_RADIUS, tableWidth - RAIL_WIDTH - BALL_RADIUS), cueBallY)

        return RackConfig(rotated, cueBallPos)
    }
}

data class RackConfig(
    val ballPositions: List<Offset>, // 15 racked positions
    val cueBallPos: Offset,
)
