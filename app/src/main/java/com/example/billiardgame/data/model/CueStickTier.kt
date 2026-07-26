package com.example.billiardgame.data.model

enum class CueStickTier(
    val displayName: String,
    val powerControl: Float,       // 0.0 - 1.0; higher = more precise shot power
    val spinAccuracy: Float,       // 0.0 - 1.0; higher = side-spin maps more accurately
    val unlockGamesWon: Int,       // Number of total wins required
    val glowColor: Long,           // ARGB color for visual glow effect during aim
    val description: String,
) {
    BRONZE("Bronze Cue",      0.50f, 0.40f, 0, 0xFF8B4513L, "Basic cue. Unlocked by default."),
    SILVER("Silver Cue",      0.65f, 0.55f, 1, 0xFFC0C0C0L, "Better control. Win 1 game to unlock."),
    GOLD("Gold Cue",          0.80f, 0.70f, 5, 0xFFFFD700L, "Precise shots. Win 5 games to unlock."),
    DIAMOND("Diamond Cue",    0.90f, 0.85f, 15, 0xFFB9F2FFL, "Expert precision. Win 15 games to unlock."),
    LEGENDARY("Legendary Cue",1.00f, 1.00f, 30, 0xFFFF69B4L, "Maximum power and accuracy. Win 30 games to unlock.");

    companion object {
        fun getAvailableTiers(totalWins: Int): List<CueStickTier> =
            values().filter { it.unlockGamesWon <= totalWins }
    }
}
