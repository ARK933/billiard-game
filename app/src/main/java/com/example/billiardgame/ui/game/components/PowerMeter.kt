package com.example.billiardgame.ui.game.components
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billiardgame.data.model.CueStickTier

@Composable
fun PowerMeter(power: Float, tier: CueStickTier) {
    val colors = listOf(Color(0xFF2E7D32), Color(0xFFFFD700), Color(0xFFD32F2F))
    val meterW = 14.dp
    val maxH = 80.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(end = 4.dp).align(Alignment.CenterEnd),
    ) {
        Box(
            modifier = Modifier
                .width(meterW)
                .height(maxH)
                .background(Color.Black.copy(alpha = 0.5f)),
        ) {
            // Gradient fill from bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(power)
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(colors = colors)
                    ),
            ) {}

            // Glowing border
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 1.5.dp,
                        color = Color(tier.glowColor).copy(alpha = 0.3f + power * 0.6f),
                    ),
            )
        }

        Text(
            text = "${(power * 100).toInt()}%",
            color = Color.White,
            fontSize = 9.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}
