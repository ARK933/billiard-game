package com.example.billiardgame.ui.cueshifts

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billiardgame.data.model.CueStickTier

@Composable
fun CueStickScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cue Stick Collection", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A1A1A)),
            )
        },
        containerColor = Color(0xFF1A1A1A),
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(CueStickTier.entries) { tier ->
                CueStickCard(tier)
            }
        }
    }
}

@Composable
fun CueStickCard(tier: CueStickTier) {
    val isUnlocked = true // TODO: check from repository

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Cue stick visual (colored bar)
                Box(
                    modifier = Modifier
                        .size(60.dp, 8.dp)
                        .background(
                            brush = androidx.compose.ui.graphics.graphicsLayer {}.let {
                                androidx.compose.foundation.background(
                                    brush = androidx.compose.ui.graphics.drawmatrix.TransformAwareBrush.linearGradient(
                                        colors = listOf(Color(tier.glowColor), Color(0xFF444444)),
                                    )
                                )
                            },
                            shape = RoundedCornerShape(4.dp),
                        ),
                ) {}

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = tier.displayName,
                        color = Color(tier.glowColor),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = tier.description,
                        color = Color.Gray,
                        fontSize = 11.sp,
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                if (isUnlocked) {
                    Text(
                        text = "✓ Unlocked",
                        color = Color(0xFF4CAF50),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    )
                } else {
                    Text(
                        text = "🔒 ${tier.unlockGamesWon} wins",
                        color = Color.Gray,
                        fontSize = 12.sp,
                    )
                }
            }

            // Stat bars
            Spacer(modifier = Modifier.height(8.dp))
            StatBar("Power Control", tier.powerControl, Color(tier.glowColor))
            StatBar("Spin Accuracy", tier.spinAccuracy, Color(tier.glowColor))
        }
    }
}

@Composable
private fun StatBar(label: String, value: Float, color: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, color = Color.Gray.copy(alpha = 0.7f), fontSize = 10.sp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(Color.Black.copy(0.5f), RoundedCornerShape(3.dp)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(value)
                    .height(6.dp)
                    .background(color, RoundedCornerShape(3.dp)),
            ) {}
        }
    }
}
