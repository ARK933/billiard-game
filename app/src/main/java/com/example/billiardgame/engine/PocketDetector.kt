package com.example.billiardgame.engine

import androidx.compose.ui.geometry.Offset
import com.example.billiardgame.domain.model.Ball
import com.example.billiardgame.util.Constants.*

object PocketDetector {

    fun checkPocketed(balls: List<Ball>, tableWidth: Float, tableHeight: Float): List<Offset> {
        val pocketed = mutableListOf<Offset>()
        for (ball in balls) {
            if (ball.pocketed) continue
            for (pocket in POCKET_POSITIONS) {
                if (isInPocket(ball.pos, pocket)) {
                    ball.pocketed = true
                    pocketed.add(ball.pos)
                    break
                }
            }
        }
        return pocketed
    }

    private fun isInPocket(ballPos: Offset, pocketPos: Offset): Boolean {
        val dx = ballPos.x - pocketPos.x
        val dy = ballPos.y - pocketPos.y
        return dx * dx + dy * dy < POCKET_RADIUS * POCKET_RADIUS
    }
}
