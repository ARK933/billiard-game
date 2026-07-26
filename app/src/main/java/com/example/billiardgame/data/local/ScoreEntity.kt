package com.example.billiardgame.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "score_entries")
data class ScoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateMillis: Long,
    val result: String,              // "WIN" or "LOSS"
    val opponent: String = "AI",
    val cueStickUsed: String = "BRONZE",
    val ballsPocketed: Int = 0,
    val foulsCommitted: Int = 0,
)
