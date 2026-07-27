package com.example.billiardgame.ui.menu
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameResultDialog(
    winner: String,
    onPlayAgain: () -> Unit,
    onBackToMenu: () -> Unit,
) {
    Dialog(
        onDismissRequest = {},
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(24.dp),
            ) {
                Text(
                    text = winner,
                    color = if (winner.contains("Win")) Color(0xFF4CAF50) else Color(0xFFFF5252),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = "Great game!",
                    color = Color.Gray,
                    fontSize = 14.sp,
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onPlayAgain,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    ) {
                        Text("Play Again", fontSize = 14.sp)
                    }
                    OutlinedButton(onClick = onBackToMenu) {
                        Text("Menu", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
