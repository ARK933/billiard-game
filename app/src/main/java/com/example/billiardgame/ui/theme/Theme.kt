package com.example.billiardgame.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun BilliardGameTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = androidx.compose.material3.darkColorScheme(
            primary = Green80,
            secondary = WoodBrown80,
            surface = FeltDark,
            onSurface = White,
        ),
        content = content
    )
}
