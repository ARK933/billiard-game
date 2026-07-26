package com.example.billiardgame.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreDao {
    @Query("SELECT * FROM score_entries ORDER BY dateMillis DESC LIMIT 100")
    fun getAll(): Flow<List<ScoreEntity>>

    @Insert(onConflict = OnConflictStrategy.APPEND)
    suspend fun insert(entity: ScoreEntity)

    @Query("DELETE FROM score_entries")
    suspend fun deleteAll()
}
