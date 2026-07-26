package com.example.billiardgame.domain.model

import androidx.compose.ui.geometry.Offset
import com.example.billiardgame.data.model.BallColor

data class Ball(
    val number: Int,                 // 0=cue, 8=eight, 1-7=solids, 9-15=stripes
    var pos: Offset,
    var vel: Offset = Offset.Zero,
    val color: BallColor = BallColor.fromNumber(number),
    var pocketed: Boolean = false,
) {
    /** The "group" for this ball: CUE, EIGHT, SOLIDS, STRIPES */
    val group: BallGroup
        get() = when (number) {
            0 -> BallGroup.CUE
            8 -> BallGroup.EIGHT
            in 1..7 -> BallGroup.SOLIDS
            in 9..15 -> BallGroup.STRIPE
            else -> BallGroup.NONE
        }

    companion object {
        /** Create a full set of 16 balls at their starting positions */
        fun createRack(ballsAt: List<Offset>, cueBallPos: Offset): List<Ball> {
            return ballsAt.withIndex().map { (i, pos) ->
                val num = i + 1
                Ball(number = num, pos = pos)
            } + Ball(number = 0, pos = cueBallPos)
        }
    }
}

sealed interface BallGroup {
    object CUE : BallGroup
    object EIGHT : BallGroup
    object SOLIDS : BallGroup
    object STRIPE : BallGroup
    object NONE : BallGroup
}
