package com.example.billiardgame.engine

import androidx.compose.ui.geometry.Offset
import com.example.billiardgame.domain.model.Ball
import com.example.billiardgame.util.Constants.*
import com.example.billiardgame.util.MathUtils

object PhysicsEngine {

    /** Step all balls by their velocity for the given time delta */
    fun integrate(balls: List<Ball>, deltaTime: Double) {
        val dt = deltaTime.toFloat()
        for (ball in balls) {
            if (ball.pocketed) continue
            ball.pos.x += ball.vel.x * dt
            ball.pos.y += ball.vel.y * dt
        }
    }

    /** Run a full physics tick: integrate -> collide -> pocket -> friction */
    fun tick(
        balls: MutableList<Ball>,
        tableWidth: Float,
        tableHeight: Float,
        deltaTime: Double,
    ): EngineResult {
        // 1. Integrate positions
        integrate(balls, deltaTime)

        // 2. Ball-ball collisions
        for (i in balls.indices) {
            if (balls[i].pocketed) continue
            for (j in i + 1 until balls.size) {
                if (balls[j].pocketed) continue
                CollisionDetector.resolveBallBall(balls[i], balls[j])
            }
        }

        // 3. Wall collisions
        CollisionDetector.resolveWallCollisions(balls, tableWidth, tableHeight)

        // 4. Pocket detection
        val pocketedThisTick = PocketDetector.checkPocketed(balls, tableWidth, tableHeight)

        // 5. Friction
        val movingCount = FrictionCalculator.applyToAll(balls)

        return EngineResult(pocketedThisTick, movingCount == 0)
    }

    data class EngineResult(
        val pocketed: List<Offset>,
        val allStopped: Boolean,
    )
}
