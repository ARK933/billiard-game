package com.example.billiardgame.util

import androidx.compose.ui.geometry.Offset

object Constants {
    // Table dimensions (logical, scaled to screen)
    const val TABLE_WIDTH = 700f
    const val TABLE_HEIGHT = 1400f
    const val RAIL_WIDTH = 60f
    const val BALL_RADIUS = 18f
    const val POCKET_RADIUS = 32f

    // Physics
    const val FRICTION_COEFFICIENT = 0.985f          // Per-frame velocity damping at 120 FPS
    const val RESTITUTION_BALL = 0.95f               // Ball-ball elastic collision
    const val RESTITUTION_WALL = 0.85f               // Wall bounce restitution
    const val STOP_THRESHOLD = 0.5f                  // Velocity below which ball is considered stopped
    const val MAX_POWER = 25f                         // Max shot power (pixels/sec)
    const val MIN_POWER = 3f                          // Min shot power
    const val PHYSICS_TICK_RATE = 1.0 / 120.0        // Fixed timestep in seconds

    // Pocket positions (relative to table origin 0,0 at top-left of playing surface)
    val POCKET_POSITIONS = listOf(
        Offset(0f, 0f),        // top-left
        Offset(TABLE_WIDTH / 2, 0f),  // top-center
        Offset(TABLE_WIDTH, 0f),      // top-right
        Offset(0f, TABLE_HEIGHT),   // bottom-left
        Offset(TABLE_WIDTH / 2, TABLE_HEIGHT), // bottom-center
        Offset(TABLE_WIDTH, TABLE_HEIGHT),     // bottom-right
    )

    // Scoring
    const val SOLID_POINTS = 2
    const val STRIPE_POINTS = 3
    const val EIGHT_POINT = 5

    // Touch
    const val TOUCH_ZOOM_RADIUS = BALL_RADIUS * 4f
    const val AIM_LINE_MAX_SEGMENTS = 5
}
