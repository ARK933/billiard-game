package com.example.billiardgame.ui.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billiardgame.data.local.ScoreEntity
import com.example.billiardgame.data.repository.ScoreRepository
import com.example.billiardgame.di.AppModule
import dagger.hilt.android.EntryPointAccessors
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ScoreboardScreen(
    onBack: () -> Unit,
) {
    var stats by remember { mutableStateOf(ScoreRepository.GameStats()) }
    var entries by remember { mutableStateOf(emptyList<ScoreEntity>()) }
    var showClearDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // This would normally inject via Hilt, simplified for Compose preview
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stats & Records", color = Color.White) },
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
            // KPI cards
            item {
                StatKpiRow(stats)
            }

            // History header
            item {
                Text(
                    text = "Match History",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            // Empty state or history
            if (entries.isEmpty()) {
                item {
                    Text(
                        text = "No games played yet. Start playing to see your stats!",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 32.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                }
            } else {
                items(entries, key = { it.id }) { entry ->
                    MatchHistoryEntry(entry)
                }
            }

            // Clear button
            item {
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedButton(
                    onClick = { showClearDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                ) {
                    Text("Clear History")
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear History?") },
            text = { Text("This will delete all recorded games. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { /* clear */ showClearDialog = false }) {
                    Text("Clear", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
private fun StatKpiRow(stats: ScoreRepository.GameStats) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            StatCard("Games", stats.totalGames.toString(), Modifier.weight(1f))
            StatCard("Wins", stats.totalWins.toString(), Modifier.weight(1f))
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        ) {
            StatCard("Streak", "${stats.currentWinStreak}", Modifier.weight(1f))
            StatCard("Best", "${stats.bestWinStreak}", Modifier.weight(1f))
            StatCard("Rate", "%.0f%%".format(stats.winRate * 100), Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp),
        ) {
            Text(text = value, color = Color(0xFFFFD700), fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(text = label, color = Color.Gray, fontSize = 11.sp)
        }
    }
}

@Composable
private fun MatchHistoryEntry(entry: ScoreEntity) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = if (entry.result == "WIN") "WIN" else "LOSS",
                    color = if (entry.result == "WIN") Color(0xFF4CAF50) else Color(0xFFFF5252),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Cue: ${entry.cueStickUsed}",
                    color = Color.Gray,
                    fontSize = 11.sp,
                )
            }
            Text(
                text = "${entry.ballsPocketed} balls",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp,
            )
        }
    }
}
