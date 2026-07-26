package com.example.billiardgame.data.repository

import com.example.billiardgame.data.local.ScoreDao
import com.example.billiardgame.data.local.ScoreEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface ScoreRepository {
    val allEntries: Flow<List<ScoreEntity>>
    val stats: Flow<GameStats>
    suspend fun recordGame(result: String, cueStickUsed: String, ballsPocketed: Int, fouls: Int)
    suspend fun clearHistory()

    data class GameStats(
        val totalGames: Int = 0,
        val totalWins: Int = 0,
        val currentWinStreak: Int = 0,
        val bestWinStreak: Int = 0,
        val winRate: Float = 0f,
    )

    companion object {
        fun computeStats(entries: List<ScoreEntity>): GameStats {
            val totalGames = entries.size
            if (totalGames == 0) return GameStats()

            val wins = entries.count { it.result == "WIN" }
            val winRate = wins.toFloat() / totalGames

            var currentStreak = 0
            var bestStreak = 0
            for (entry in entries.reversed()) {
                if (entry.result == "WIN") {
                    currentStreak++
                    bestStreak = maxOf(bestStreak, currentStreak)
                } else {
                    currentStreak = 0
                }
            }

            return GameStats(
                totalGames = totalGames,
                totalWins = wins,
                currentWinStreak = currentStreak,
                bestWinStreak = bestStreak,
                winRate = winRate,
            )
        }
    }
}

class ScoreRepositoryImpl(
    private val scoreDao: ScoreDao,
) : ScoreRepository {

    override val allEntries: Flow<List<ScoreEntity>> = scoreDao.getAll()

    override val stats: Flow<ScoreRepository.GameStats> = allEntries.map { entries ->
        ScoreRepository.computeStats(entries)
    }

    override suspend fun recordGame(
        result: String,
        cueStickUsed: String,
        ballsPocketed: Int,
        fouls: Int,
    ) {
        scoreDao.insert(
            ScoreEntity(
                dateMillis = System.currentTimeMillis(),
                result = result,
                cueStickUsed = cueStickUsed,
                ballsPocketed = ballsPocketed,
                foulsCommitted = fouls,
            ),
        )
    }

    override suspend fun clearHistory() = scoreDao.deleteAll()
}
