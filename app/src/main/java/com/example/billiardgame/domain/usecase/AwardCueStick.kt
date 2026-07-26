package com.example.billiardgame.domain.usecase

import com.example.billiardgame.data.model.CueStickTier
import com.example.billiardgame.data.repository.ScoreRepository
import com.example.billiardgame.domain.model.BallGroup
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AwardCueStick @Inject constructor(
    private val repository: ScoreRepository,
) {
    /** Check if the player should be awarded a new cue stick tier after a win */
    suspend fun checkAndAward(currentEquippedTier: CueStickTier): Result<AwardResult> {
        val totalWins = repository.stats.first().totalWins
        val nextTierIndex = currentEquippedTier.ordinal + 1

        return if (nextTierIndex < CueStickTier.values().size) {
            val nextTier = CueStickTier.values()[nextTierIndex]
            if (totalWins >= nextTier.unlockGamesWon) {
                AwardResult.NewTierUnlocked(nextTier, totalWins)
            } else {
                AwardResult.NoUnlockYet(totalWins, nextTier)
            }
        } else {
            AwardResult.MaxTierReached(totalWins)
        }
    }

    sealed interface AwardResult {
        data class NewTierUnlocked(val tier: CueStickTier, val totalWins: Int) : AwardResult
        data class NoUnlockYet(val totalWins: Int, val nextTier: CueStickTier) : AwardResult
        object MaxTierReached : AwardResult
    }
}
