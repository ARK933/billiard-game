package com.example.billiardgame.domain.model

enum class ShotResult {
    OK, CUE_POCKETED, EIGHT_EARLY, EIGHT_LATE_WIN, OK_KEEP_TURN, OK_CHANGE_TURN
}

data class GameState(
    val balls: List<Ball>,
    val tableWidth: Float,
    val tableHeight: Float,
    var phase: GamePhase = GamePhase.IDLE,
    var currentTurn: PlayerType = PlayerType.HUMAN,
    var humanGroup: BallGroup = BallGroup.NONE,
    var aiGroup: BallGroup = BallGroup.NONE,
    var firstLegalShotTaken: Boolean = false,
    var cueBallHand: Boolean = false, // true if human has ball-in-hand
    var lastPocketedBalls: List<Ball> = emptyList(),
    var foulsThisShot: Int = 0,
    var humanBallsPocketed: Int = 0,
    var aiBallsPocketed: Int = 0,
    var aiThinking: Boolean = false,
)

enum class GamePhase {
    IDLE,
    AIMING,
    CHARGING,
    SHOOTING,
    BALL_SETTLING,
    PROCESS_RESULT,
    GAME_OVER,
}

enum class PlayerType {
    HUMAN, AI
}
