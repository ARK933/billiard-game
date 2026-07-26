package com.example.billiardgame.ui.menu

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.billiardgame.data.repository.ScoreRepository
import com.example.billiardgame.data.model.CueStickTier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainMenuViewModel @Inject constructor(
    private val repository: ScoreRepository,
) : ViewModel() {

    var totalWins by mutableStateOf(0)
        private set
    var equippedCue by mutableStateOf(CueStickTier.BRONZE)
        private set

    init {
        viewModelScope.launch {
            repository.stats.collect { stats ->
                totalWins = stats.totalWins
                equippedCue = CueStickTier.values().lastOrNull { tier ->
                    stats.totalWins >= tier.unlockGamesWon
                } ?: CueStickTier.BRONZE
            }
        }
    }
}
