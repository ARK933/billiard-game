package com.example.billiardgame.engine

import com.example.billiardgame.domain.model.Ball
import com.example.billiardgame.util.Constants.*

object FrictionCalculator {

    /** Apply per-frame friction damping. Returns true if ball should be considered stopped */
    fun applyFriction(ball: Ball) {
        val speed = kotlin.math.sqrt(ball.vel.x * ball.vel.x + ball.vel.y * ball.vel.y)
        if (speed <= STOP_THRESHOLD) {
            ball.vel.x = 0f
            ball.vel.y = 0f
            return
        }
        // Scale velocity by friction coefficient
        val scale = FRICTION_COEFFICIENT
        ball.vel.x *= scale
        ball.vel.y *= scale

        // Double-check: if below threshold after damping, stop completely
        val newSpeed = kotlin.math.sqrt(ball.vel.x * ball.vel.x + ball.vel.y * ball.vel.y)
        if (newSpeed <= STOP_THRESHOLD) {
            ball.vel.x = 0f
            ball.vel.y = 0f
        }
    }

    /** Apply friction to all balls. Returns count of still-moving balls */
    fun applyToAll(balls: List<Ball>): Int {
        var movingCount = 0
        for (ball in balls) {
            if (!ball.pocketed) {
                applyFriction(ball)
                if (kotlin.math.sqrt(ball.vel.x * ball.vel.x + ball.vel.y * ball.vel.y) > STOP_THRESHOLD) {
                    movingCount++
                }
            }
        }
        return movingCount
    }
}
