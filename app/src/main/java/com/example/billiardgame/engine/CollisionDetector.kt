package com.example.billiardgame.engine

import androidx.compose.ui.geometry.Offset
import com.example.billiardgame.domain.model.Ball
import com.example.billiardgame.util.Constants.*
import kotlin.math.max

object CollisionDetector {

    /** Resolve elastic collision between two equal-mass balls */
    fun resolveBallBall(a: Ball, b: Ball) {
        val dx = b.pos.x - a.pos.x
        val dy = b.pos.y - a.pos.y
        val distSq = dx * dx + dy * dy
        val minDist = BALL_RADIUS * 2

        if (distSq >= minDist * minDist || distSq == 0f) return

        val dist = kotlin.math.sqrt(distSq)
        // Normal from a to b
        val nx = dx / dist
        val ny = dy / dist

        // Relative velocity along normal
        val dvx = a.vel.x - b.vel.x
        val dvy = a.vel.y - b.vel.y
        val dvn = dvx * nx + dvy * ny

        // Don't resolve if velocities are separating
        if (dvn <= 0) return

        // Impulse scalar (equal mass)
        val impulse = dvn * RESTITUTION_BALL

        // Apply impulse
        a.vel.x -= impulse * nx
        a.vel.y -= impulse * ny
        b.vel.x += impulse * nx
        b.vel.y += impulse * ny

        // Separate overlapping balls
        val overlap = minDist - dist
        val halfOverlap = overlap / 2f
        a.pos.x -= halfOverlap * nx
        a.pos.y -= halfOverlap * ny
        b.pos.x += halfOverlap * nx
        b.pos.y += halfOverlap * ny
    }

    /** Check and resolve wall/boundary collisions for all balls */
    fun resolveWallCollisions(balls: List<Ball>, tableWidth: Float, tableHeight: Float) {
        for (ball in balls) {
            if (ball.pocketed) continue

            // Left wall
            if (ball.pos.x < BALL_RADIUS) {
                ball.pos.x = BALL_RADIUS
                ball.vel.x = -ball.vel.x * RESTITUTION_WALL
            }
            // Right wall
            if (ball.pos.x > tableWidth - BALL_RADIUS) {
                ball.pos.x = tableWidth - BALL_RADIUS
                ball.vel.x = -ball.vel.x * RESTITUTION_WALL
            }
            // Top wall
            if (ball.pos.y < BALL_RADIUS) {
                ball.pos.y = BALL_RADIUS
                ball.vel.y = -ball.vel.y * RESTITUTION_WALL
            }
            // Bottom wall
            if (ball.pos.y > tableHeight - BALL_RADIUS) {
                ball.pos.y = tableHeight - BALL_RADIUS
                ball.vel.y = -ball.vel.y * RESTITUTION_WALL
            }
        }
    }
}
