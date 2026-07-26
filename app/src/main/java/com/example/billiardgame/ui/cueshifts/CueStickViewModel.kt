package com.example.billiardgame.ui.cueshifts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.billiardgame.data.model.CueStickTier

class CueStickViewModel : ViewModel() {
    var equippedTier by mutableStateOf(CueStickTier.BRONZE)
    val allTiers = CueStickTier.entries

    fun equip(tier: CueStickTier) {
        equippedTier = tier
    }
}
