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
package com.example.billiardgame.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scores") // <-- 关键点：明确定义表名为 scores
data class ScoreEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val score: Int,
    val date: Long = System.currentTimeMillis()
)
