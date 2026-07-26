package com.example.billiardgame.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.billiardgame.data.repository.ScoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScoreboardViewModel @Inject constructor(
    private val repository: ScoreRepository,
) : ViewModel() {

    private val _stats = MutableStateFlow(ScoreRepository.GameStats())
    val stats: StateFlow<ScoreRepository.GameStats> = _stats

    init {
        viewModelScope.launch {
            repository.stats.collectLatest { s ->
                _stats.value = s
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}
