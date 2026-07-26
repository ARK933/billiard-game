package com.example.billiardgame.ui.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainMenuScreen(
    onNavigate: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0B6B3C), Color(0xFF0A4A2C)),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Title
            Text(
                text = "8-Ball Billiards",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            // Decorative ball row
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (color in listOf(Color(0xFFFFFF00), Color(0xFF0000FF), Color(0xFFFF0000), Color(0xFF000000))) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(color, RoundedCornerShape(50))
                            .border(2.dp, Color.White.copy(0.5f), RoundedCornerShape(50)),
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Play button
            Button(
                onClick = { onNavigate("game") },
                modifier = Modifier.width(240.dp).height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
            ) {
                Text("Play Game", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            // Cue sticks button
            OutlinedButton(
                onClick = { onNavigate("cue_sticks") },
                modifier = Modifier.width(240.dp).height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = BorderStroke(2.dp, Color.White.copy(0.6f)),
            ) {
                Text("Cue Sticks", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }

            // Stats button
            OutlinedButton(
                onClick = { onNavigate("scoreboard") },
                modifier = Modifier.width(240.dp).height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = BorderStroke(2.dp, Color.White.copy(0.6f)),
            ) {
                Text("Stats & Records", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = "Equipped: Bronze Cue",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp,
            )
        }
    }
}
