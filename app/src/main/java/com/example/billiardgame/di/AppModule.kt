package com.example.billiardgame.di

import android.content.Context
import androidx.room.Room
import com.example.billiardgame.data.local.ScoreDatabase
import com.example.billiardgame.data.repository.ScoreRepository
import com.example.billiardgame.data.repository.ScoreRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideScoreDatabase(@ApplicationContext app: Context): ScoreDatabase =
        Room.databaseBuilder(
            app,
            ScoreDatabase::class.java,
            "billiard_db",
        ).build()

    @Provides
    @Singleton
    fun provideScoreRepository(db: ScoreDatabase): ScoreRepository =
        ScoreRepositoryImpl(db.scoreDao())
}
