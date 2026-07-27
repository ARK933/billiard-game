package com.example.billiardgame.ui.game
import kotlin.math.sin
import kotlin.math.cos // 如果用到了 cos 也一并加上
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.billiardgame.data.model.CueStickTier
import com.example.billiardgame.data.repository.ScoreRepository
import com.example.billiardgame.domain.model.*
import com.example.billiardgame.engine.PhysicsEngine
import com.example.billiardgame.util.Constants.*
import com.example.billiardgame.util.MathUtils
import com.example.billiardgame.domain.usecase.AwardCueStick
import com.example.billiardgame.domain.usecase.CalculateRackPositions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sqrt
import kotlin.random.Random

data class GameUiState(
    val phase: GamePhase = GamePhase.IDLE,
    val currentTurnHuman: Boolean = true,
    val humanGroup: BallGroup = BallGroup.NONE,
    val aiGroup: BallGroup = BallGroup.NONE,
    val humanBallsPocketed: Int = 0,
    val aiBallsPocketed: Int = 0,
    val message: String = "",
    val equippedCue: CueStickTier = CueStickTier.BRONZE,
    val nextCueUnlock: CueStickTier? = CueStickTier.SILVER,
    val cueStickReward: CueStickTier? = null,
    val currentPower: Float = 0f,
    val aimDirX: Float = 0f,
    val aimDirY: Float = 0f,
    val pullBack: Float = 0f,
    val gameOver: Boolean = false,
    val winner: String? = null,
)

@HiltViewModel
class GameViewModel @Inject constructor(
    private val scoreRepository: ScoreRepository,
    private val awardCueStick: AwardCueStick,
) : ViewModel() {

    // Game state managed locally
    private var _balls = mutableListOf<Ball>()
    private var _state = createFreshState()
    private var _equippedCue = CueStickTier.BRONZE
    private var _totalWins = 0

    // UI state exposed to Compose
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    // Physics animation loop
    private var physicsJob: kotlinx.coroutines.Job? = null

    init {
        loadStats()
        startNewGame()
    }

    private fun loadStats() {
        viewModelScope.launch {
            scoreRepository.stats.collect { stats ->
                _totalWins = stats.totalWins
                _equippedCue = CueStickTier.values().lastOrNull { tier ->
                    stats.totalWins >= tier.unlockGamesWon
                } ?: CueStickTier.BRONZE

                _uiState.update { current ->
                    current.copy(
                        equippedCue = _equippedCue,
                        nextCueUnlock = if (_equippedCue.ordinal < CueStickTier.values().size - 1) {
                            CueStickTier.values()[_equippedCue.ordinal + 1]
                        } else null,
                    )
                }
            }
        }
    }

    private fun createFreshState(): GameState {
        return GameState(
            balls = emptyList(),
            tableWidth = TABLE_WIDTH,
            tableHeight = TABLE_HEIGHT,
            phase = GamePhase.IDLE,
            currentTurn = PlayerType.HUMAN,
            humanGroup = BallGroup.NONE,
            aiGroup = BallGroup.NONE,
            firstLegalShotTaken = false,
            cueBallHand = false,
            lastPocketedBalls = emptyList(),
            foulsThisShot = 0,
            humanBallsPocketed = 0,
            aiBallsPocketed = 0,
            aiThinking = false,
        )
    }

    private fun generateRack(): RackConfig {
        val rng = Random(System.currentTimeMillis())
        // Shuffle ball numbers for non-8, non-cue balls
        val numbers = (1..15).toMutableList()
        numbers.shuffle(rng)

        val footX = TABLE_WIDTH / 2f
        val footY = TABLE_HEIGHT * 0.72f
        val spacing = BALL_RADIUS * 2.1f * (0.95f + rng.nextFloat() * 0.1f) // ±5% variation
        val rotation = rng.nextFloat() * 10f - 5f // -5 to +5 degrees

        val positions = mutableListOf<Offset>()
        var rowIdx = 0
        for (row in 0 until 5) {
            for (col in 0 until row + 1) {
                var x = footX - row * spacing * sqrt(3.0) / 2 + col * spacing
                var y = footY - row * spacing
                // Jitter
                x += rng.nextFloat() * 4f - 2f
                y += rng.nextFloat() * 4f - 2f
                // Rotate around foot center
                val angle = rotation * Math.PI / 180
                cos(angle)
                x = footX + (x - footX) * cos(angle) - (y - footY) * sin(angle)
                y = footY + (x - footX) * sin(angle) + (y - footY) * cos(angle)
                positions.add(Offset(x, y))
            }
        }

        // Ensure position 3 (front left of rack) is a solid, position 5 (front right) is a stripe
        // Simple constraint: swap if needed

        // Cue ball at head string
        val cueX = TABLE_WIDTH / 2f + rng.nextFloat() * 60f - 30f
        val cueY = TABLE_HEIGHT * 0.22f
        val cuePos = Offset(
            RAIL_WIDTH + BALL_RADIUS + rng.nextFloat() * 80f,
            cueY
        )

        // Create balls
        val ballNumbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
        _balls = (0 until 15).map { i ->
            Ball(number = ballNumbers[i], pos = positions[i])
        }.toMutableList()
        _balls.add(Ball(number = 0, pos = cuePos))

        return RackConfig(positions, cuePos)
    }

    fun startNewGame() {
        _state = createFreshState()
        generateRack()
        _state = _state.copy(balls = _balls.toList())
        _uiState.update {
            it.copy(
                phase = GamePhase.IDLE,
                currentTurnHuman = true,
                humanGroup = BallGroup.NONE,
                aiGroup = BallGroup.NONE,
                humanBallsPocketed = 0,
                aiBallsPocketed = 0,
                gameOver = false,
                winner = null,
                cueStickReward = null,
                currentPower = 0f,
                aimDirX = 0f,
                aimDirY = 0f,
                pullBack = 0f,
                message = "Tap the white cue ball to aim",
            )
        }
    }

    // --- Touch Handling ---
    fun onTouchDown(offset: Offset) {
        if (_state.phase != GamePhase.IDLE || _state.currentTurn != PlayerType.HUMAN) return

        if (_state.cueBallHand) {
            // Ball-in-hand placement
            val x = offset.x.coerceIn(RAIL_WIDTH.toFloat(), TABLE_WIDTH - RAIL_WIDTH)
            val y = offset.y.coerceIn(RAIL_WIDTH.toFloat(), TABLE_HEIGHT - RAIL_WIDTH)
            _balls.find { it.number == 0 }?.let {
                it.pos = Offset(x, y)
                it.vel = Offset.Zero
            }
            _state.cueBallHand = false
            _state.phase = GamePhase.IDLE
            _uiState.update { it.copy(message = "Tap white ball to aim") }
            return
        }

        // Check proximity to cue ball
        val cueBall = _balls.find { it.number == 0 && !it.pocketed } ?: return
        val dx = offset.x - (RAIL_WIDTH + cueBall.pos.x / TABLE_WIDTH * (TABLE_WIDTH - 2 * RAIL_WIDTH)).coerceAtLeast(RAIL_WIDTH.toFloat())
        // Simplified: check if touch is anywhere on screen during IDLE + HUMAN turn = start aiming
        _state.phase = GamePhase.AIMING
        _uiState.update { it.copy(phase = GamePhase.AIMING, message = "Drag to aim, release to shoot") }
    }

    fun onTouchDrag(dragX: Float, dragY: Float) {
        if (_state.phase !in listOf(GamePhase.AIMING, GamePhase.CHARGING)) return

        // Calculate aim direction and power from drag
        val dragLen = sqrt(dragX * dragX + dragY * dragY)
        val power = (dragLen / 400f).coerceIn(0f, 1f)

        if (power > 0.05f) {
            _state.phase = GamePhase.CHARGING
            _uiState.update {
                it.copy(
                    phase = GamePhase.CHARGING,
                    currentPower = power,
                    pullBack = power,
                    aimDirX = -dragX / dragLen,
                    aimDirY = -dragY / dragLen,
                )
            }
        }
    }

    fun onTouchUp() {
        if (_state.phase !in listOf(GamePhase.AIMING, GamePhase.CHARGING)) {
            _state.phase = GamePhase.IDLE
            _uiState.update { it.copy(phase = GamePhase.IDLE) }
            return
        }

        val power = _uiState.value.currentPower
        if (power < 0.05f) {
            _state.phase = GamePhase.IDLE
            _uiState.update { it.copy(phase = GamePhase.IDLE) }
            return
        }

        executeShot(power, _uiState.value.aimDirX, _uiState.value.aimDirY)
    }

    private fun executeShot(power: Float, aimDirX: Float, aimDirY: Float) {
        val actualPower = MIN_POWER + (MAX_POWER - MIN_POWER) * (
            power * _equippedCue.powerControl + (1 - _equippedCue.powerControl) * Random.nextFloat() * 0.2f
        )
        val aimAngle = atan2(aimDirY, aimDirX)

        _balls.find { it.number == 0 && !it.pocketed }?.let { cue ->
            cue.vel = Offset(
                cos(aimAngle) * actualPower,
                sin(aimAngle) * actualPower,
            )
        }

        _state.phase = GamePhase.SHOOTING
        _uiState.update {
            it.copy(
                phase = GamePhase.SHOOTING,
                currentPower = 0f,
                pullBack = 0f,
                message = "Balls moving...",
            )
        }

        // Start physics loop
        startPhysicsLoop()
    }

    private var lastFrameTime = System.nanoTime()

    /** Continuous physics tick loop */
    private fun startPhysicsLoop() {
        physicsJob?.cancel()
        physicsJob = viewModelScope.launch {
            var accumulator = 0.0

            while (isActive) {
                when (_state.phase) {
                    GamePhase.SHOOTING, GamePhase.BALL_SETTLING -> {
                        val now = System.nanoTime()
                        val frameTime = (now - lastFrameTime) / 1e9
                        lastFrameTime = now

                        while (accumulator >= PHYSICS_TICK_RATE) {
                            PhysicsEngine.tick(
                                balls = _balls,
                                tableWidth = _state.tableWidth,
                                tableHeight = _state.tableHeight,
                                deltaTime = PHYSICS_TICK_RATE,
                            ).let { result ->
                                if (result.allStopped) {
                                    _state.phase = GamePhase.PROCESS_RESULT
                                }
                            }
                            accumulator -= PHYSICS_TICK_RATE
                        }

                        // Update UI state periodically
                        _uiState.update { current ->
                            current.copy(
                                humanBallsPocketed = _balls.count { it.pocketed && it.group == BallGroup.SOLIDS },
                                aiBallsPocketed = _balls.count { it.pocketed && it.group == BallGroup.STRIPE },
                            )
                        }
                    }
                    GamePhase.PROCESS_RESULT -> {
                        processShotResults()
                    }
                    GamePhase.GAME_OVER -> {
                        break
                    }
                    else -> {
                        // Check if any balls are still moving
                        val movingCount = _balls.count { !it.pocketed && (sqrt(it.vel.x * it.vel.x + it.vel.y * it.vel.y) > STOP_THRESHOLD) }
                        if (movingCount == 0) {
                            processShotResults()
                        } else {
                            break // Still moving, continue loop
                        }
                    }
                }

                if (_state.phase != GamePhase.SHOOTING && _state.phase != GamePhase.BALL_SETTLING) break

                kotlinx.coroutines.delay(16) // ~60fps rendering cap
            }
        }
    }

    private suspend fun lastTimeTo(now: Long) {} // no-op, just to satisfy compiler

    private fun processShotResults() {
        val pocketed = _balls.filter { it.pocketed && it !in _state.lastPocketedBalls }
        _state.lastPocketedBalls = pocketed

        val cuePocketed = pocketed.any { it.number == 0 }
        val eightPocketed = pocketed.any { it.number == 8 }

        when {
            cuePocketed -> {
                // Foul: cue ball pocketed
                _state.foulsThisShot++
                _state.cueBallHand = true
                _state.currentTurn = PlayerType.AI // Opponent gets ball in hand
                _uiState.update { it.copy(message = "Foul! Cue ball pocketed.") }
            }
            eightPocketed -> {
                if (_state.humanGroup == BallGroup.SOLIDS && _state.humanBallsPocketed >= 7) {
                    endGame("WIN")
                } else if (_state.aiGroup == BallGroup.STRIPE && _state.aiBallsPocketed >= 7) {
                    endGame("LOSS")
                } else {
                    // Early 8-ball pocket = loss
                    endGame("LOSS")
                }
                return
            }
        }

        // Determine turn change based on first legal shot
        if (!_state.firstLegalShotTaken && pocketed.isNotEmpty()) {
            _state.firstLegalShotTaken = true
            val firstPocketed = pocketed.firstOrNull { it.group != BallGroup.EIGHT && it.group != BallGroup.CUE }
            if (firstPocketed != null) {
                if (firstPocketed.group == BallGroup.SOLIDS) {
                    _state.humanGroup = BallGroup.SOLIDS
                    _state.aiGroup = BallGroup.STRIPE
                } else {
                    _state.humanGroup = BallGroup.STRIPE
                    _state.aiGroup = BallGroup.SOLIDS
                }
            }
        }

        // Normal shot result: own pocketed = keep turn, opponent's = switch
        val hasOwnPocketed = pocketed.any { it.group == _state.humanGroup && _state.humanGroup != BallGroup.NONE }
        val hasOpponentPocketed = pocketed.any { it.group == _state.aiGroup && _state.aiGroup != BallGroup.NONE }

        if (hasOwnPocketed && !hasOpponentPocketed) {
            _state.currentTurn = PlayerType.HUMAN
            _uiState.update { it.copy(currentTurnHuman = true, message = "Great shot! Your turn again.") }
        } else {
            _state.currentTurn = PlayerType.AI
            _uiState.update { it.copy(currentTurnHuman = false, message = "AI's turn...") }
            scheduleAIShot()
        }

        _state.phase = GamePhase.IDLE
        _uiState.update { it.copy(phase = GamePhase.IDLE) }
    }

    /** Schedule AI shot after a delay */
    private fun scheduleAIShot() {
        viewModelScope.launch {
            kotlinx.coroutines.delay(800) // Thinking time
            if (_state.phase != GamePhase.IDLE) return@launch

            // Find target ball
            val target = _balls.firstOrNull { !it.pocketed && it.number != 0 && it.number != 8 }
                ?: _balls.firstOrNull { !it.pocketed && it.number == 8 }
                ?: return@launch

            val cueBall = _balls.find { it.number == 0 && !it.pocketed } ?: return@launch

            // Aim toward nearest pocket from target
            var bestPocket = POCKET_POSITIONS[0]
            for (p in POCKET_POSITIONS) {
                if (MathUtils.distance(target.pos, p) < MathUtils.distance(bestPocket, target.pos)) {
                    bestPocket = p
                }
            }

            // Angle from cue to target
            val angleToTarget = atan2(target.pos.y - cueBall.pos.y, target.pos.x - cueBall.pos.x)
            val angleToPocket = atan2(bestPocket.y - target.pos.y, bestPocket.x - target.pos.x)
            val totalAngle = angleToTarget + angleToPocket / 2 // Simplified reflection

            val power = MIN_POWER + Random.nextFloat() * (MAX_POWER - MIN_POWER) * 0.5f
            val aimOffset = Random.nextFloat() * 0.15f - 0.075f // Small inaccuracy

            cueBall.vel = Offset(
                cos(totalAngle + aimOffset) * power,
                sin(totalAngle + aimOffset) * power,
            )

            _state.phase = GamePhase.SHOOTING
            _uiState.update { it.copy(phase = GamePhase.SHOOTING, message = "AI shoots...") }
            startPhysicsLoop()
        }
    }

    private fun endGame(result: String) {
        _state.phase = GamePhase.GAME_OVER
        _uiState.update {
            it.copy(
                phase = GamePhase.GAME_OVER,
                gameOver = true,
                winner = if (result == "WIN") "You Win!" else "AI Wins!",
                message = if (result == "WIN") "Congratulations! You won!" else "Better luck next time.",
            )
        }

        // Record score and check cue stick reward
        viewModelScope.launch {
            val pocketedCount = _balls.count { it.pocketed && it.group != BallGroup.CUE && it.group != BallGroup.EIGHT }
            scoreRepository.recordGame(
                result = result,
                cueStickUsed = _equippedCue.name,
                ballsPocketed = pocketedCount,
                fouls = _state.foulsThisShot,
            )

            // Check cue stick unlock
            awardCueStick.checkAndAward(_equippedCue).getOrNull()?.let { award ->
                when (award) {
                    is AwardCueStick.AwardResult.NewTierUnlocked -> {
                        _equippedCue = award.tier
                        _uiState.update { current ->
                            current.copy(
                                equippedCue = _equippedCue,
                                cueStickReward = award.tier,
                                nextCueUnlock = if (award.tier.ordinal < CueStickTier.values().size - 1) {
                                    CueStickTier.values()[award.tier.ordinal + 1]
                                } else null,
                            )
                        }
                    }
                    else -> { /* Already at max or no unlock yet */ }
                }
            }
        }
    }
}
