package com.example.billiardgame.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.billiardgame.data.model.CueStickTier
import com.example.billiardgame.domain.model.BallGroup
import com.example.billiardgame.domain.model.GamePhase
import com.example.billiardgame.ui.game.components.PowerMeter
import com.example.billiardgame.util.Constants.*
import kotlinx.coroutines.delay
import kotlin.math.min
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.atan2

@Composable
fun GameScreen(
    onBack: () -> Unit,
    viewModel: GameViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    var showCueReward by remember { mutableStateOf<CueStickTier?>(null) }

    LaunchedEffect(state.cueStickReward) {
        state.cueStickReward?.let { tier ->
            showCueReward = tier
            delay(4000)
            showCueReward = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val balls = viewModel.balls
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset -> viewModel.onTouchDown(offset) },
                        onDrag = { change, dragAmount -> viewModel.onTouchDrag(dragAmount.x, dragAmount.y) },
                        onDragEnd = { viewModel.onTouchUp() },
                        onDragCancel = { viewModel.onTouchUp() },
                    )
                },
        ) {
            drawGameTable(balls, state, this)
        }

        // Top score bar
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .background(Color(0xAA000000))
                .padding(horizontal = 12.dp, vertical = 6.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "You ${if (state.humanGroup == BallGroup.SOLIDS) "● Solids" else if (state.humanGroup == BallGroup.STRIPE) "◐ Stripes" else "?"}",
                    color = Color.White, fontSize = 11.sp,
                )
                Text(
                    text = if (state.currentTurnHuman && !state.gameOver) "YOUR TURN" else "",
                    color = if (state.currentTurnHuman && !state.gameOver) Color(0xFFFFD700) else Color.Transparent,
                    fontSize = 11.sp, fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "AI ${if (state.aiGroup == BallGroup.SOLIDS) "● Solids" else if (state.aiGroup == BallGroup.STRIPE) "◐ Stripes" else "?"}",
                    color = Color.White, fontSize = 11.sp,
                )
            }
        }

        // Message overlay
        if (state.message.isNotEmpty() && !state.gameOver) {
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 60.dp).wrapContentWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xDD000000),
            ) {
                Text(text = state.message, color = Color.White, fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            }
        }

        // Cue unlock notification
        showCueReward?.let { tier ->
            Card(
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 50.dp).fillMaxWidth(0.8f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(tier.glowColor)),
            ) {
                Text(text = "New Cue: ${tier.displayName}!", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
        }

        // Power meter
        if (state.phase == GamePhase.AIMING || state.phase == GamePhase.CHARGING) {
            PowerMeter(power = state.currentPower, tier = state.equippedCue)
        }

        // Game over dialog
        if (state.gameOver && state.winner != null) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text(state.winner ?: "", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                text = { Text("Tap below to continue") },
                confirmButton = { Button(onClick = { viewModel.startNewGame() }) { Text("Play Again") } },
                dismissButton = { TextButton(onClick = onBack) { Text("Menu") } },
            )
        }

        // Back button
        IconButton(onClick = onBack, modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
            )
        }
    }
}

private fun DrawScope.drawGameTable(
    balls: List<com.example.billiardgame.domain.model.Ball>,
    state: GameUiState,
    ds: DrawScope,
) {
    val w = size.width
    val h = size.height

    // Wood border
    drawRect(Color(0xFF6B3E1F))

    // Felt
    val feltInset = RAIL_WIDTH.coerceAtMost(w, h) / 2
    drawRect(feltInset, feltInset, w - feltInset, h - feltInset, Color(0xFF0B6B3C))

    // Scale factor
    val scale = min((w - 2 * feltInset) / TABLE_WIDTH, (h - 2 * feltInset) / TABLE_HEIGHT)

    // Pockets
    for (pocket in POCKET_POSITIONS) {
        drawCircle(
            color = Color(0xFF0A0A0A),
            radius = POCKET_RADIUS * scale,
            center = Offset(feltInset + pocket.x * scale, feltInset + pocket.y * scale),
        )
    }

    // Pocketed balls row at top
    val pocketedAll = balls.filter { it.pocketed }
    if (pocketedAll.isNotEmpty()) {
        val ballR = scale * BALL_RADIUS * 0.5f
        val spacing = ballR * 2.5f
        val startX = w / 2f - (pocketedAll.size - 1) * spacing / 2f
        pocketedAll.forEachIndexed { idx, ball ->
            val cx = startX + idx * spacing
            drawBallAt(ball, Offset(cx, 25f), ballR)
        }
    }

    // Active balls sorted by Y
    val activeBalls = balls.filterNot { it.pocketed }.sortedBy { it.pos.y }
    val ballR = scale * BALL_RADIUS
    for (ball in activeBalls) {
        drawBallAt(
            ball,
            Offset(feltInset + ball.pos.x * scale, feltInset + ball.pos.y * scale),
            ballR,
        )
    }

    // Aim line + cue stick
    if (state.phase == GamePhase.AIMING || state.phase == GamePhase.CHARGING) {
        val cueBall = balls.find { it.number == 0 && !it.pocketed } ?: return
        val cueCx = feltInset + cueBall.pos.x * scale
        val cueCy = feltInset + cueBall.pos.y * scale

        // Aim line (dotted)
        val dirX = state.aimDirX
        val dirY = state.aimDirY
        var lx = cueCx
        var ly = cueCy
        for (i in 0 until AIM_LINE_MAX_SEGMENTS) {
            if (i % 2 == 0) {
                drawLine(
                    color = Color(0xFFFFFFFF).copy(alpha = 0.5f + state.currentPower * 0.5f),
                    start = Offset(lx, ly),
                    end = Offset(lx + dirX * 12f, ly + dirY * 12f),
                    strokeWidth = 2f,
                )
            }
            lx += dirX * 25f
            ly += dirY * 25f
        }

        // Cue stick glow
        drawCircle(
            color = Color(state.equippedCue.glowColor).copy(alpha = state.currentPower * 0.4f),
            radius = ballR * 4f,
            center = Offset(cueCx, cueCy),
        )

        // Cue stick body
        val stickAngle = atan2(dirY, dirX)
        val dirXd = -cos(stickAngle)
        val dirYd = -sin(stickAngle)
        val stickLen = min(w, h) * 0.3f
        val baseDist = ballR * 2 + state.pullBack * state.currentPower * 15f
        val shaftX = cueCx + dirXd * baseDist
        val shaftY = cueCy + dirYd * baseDist
        val endX = shaftX + dirXd * stickLen
        val endY = shaftY + dirYd * stickLen

        drawLine(Color(0xFFFFD700), Offset(shaftX, shaftY), Offset(shaftX + dirXd * ballR, shaftY + dirYd * ballR), ballR * 1.5f, style = Stroke)
        drawLine(Color(0xFFF5F0E1), Offset(shaftX + dirXd * ballR, shaftY + dirYd * ballR), Offset(endX, endY), 3f)
        drawLine(Color(0xFF8B4513), Offset(endX - dirXd * stickLen * 0.4f, endY - dirYd * stickLen * 0.4f), Offset(endX, endY), 8f)
    }
}

private fun DrawScope.drawBallAt(ball: com.example.billiardgame.domain.model.Ball, center: Offset, radius: Float) {
    // Shadow
    drawCircle(Color(0x33000000), radius, center + Offset(2f, 2f))
    // Base color
    drawCircle(Color(ball.color.color), radius, center)
    // Stripe band
    if (ball.number in 9..15) {
        drawCircle(Color(0xFFF5F5F5), radius * 0.88f, center)
        drawCircle(Color(ball.color.color), radius * 0.6f, center)
    }
    // Specular highlight
    drawCircle(Color(0x44FFFFFF), radius * 0.3f, center - Offset(radius * 0.25f, radius * 0.25f))
    // Number background circle
    if (ball.number != 0) {
        drawCircle(Color(0xFFFFFFEE), radius * 0.35f, center)
    }
}
