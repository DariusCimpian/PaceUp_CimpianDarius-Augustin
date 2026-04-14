package com.example.paceup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paceup.data.repository.WarRepository
import com.example.paceup.model.ClanWar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WarViewModel : ViewModel() {
    private val repository = WarRepository()

    private val _activeWar = MutableStateFlow<ClanWar?>(null)
    val activeWar: StateFlow<ClanWar?> = _activeWar

    private val _pendingWars = MutableStateFlow<List<ClanWar>>(emptyList())
    val pendingWars: StateFlow<List<ClanWar>> = _pendingWars

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadWars(clanId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val activeResult = repository.getActiveWar(clanId)
                if (activeResult.isSuccess) _activeWar.value = activeResult.getOrNull()
                val pendingResult = repository.getPendingWars(clanId)
                if (pendingResult.isSuccess) _pendingWars.value = pendingResult.getOrNull() ?: emptyList()
            } catch (e: Exception) {
                _message.value = "Eroare la încărcare: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sendChallenge(
        myClanId: String,
        myClanName: String,
        targetClanId: String,
        targetClanName: String,
        days: Int
    ) {
        viewModelScope.launch {
            try {
                val result = repository.sendWarChallenge(myClanId, myClanName, targetClanId, targetClanName, days)
                _message.value = if (result.isSuccess) "⚔️ Provocare trimisă!" else "Eroare: ${result.exceptionOrNull()?.message}"
            } catch (e: Exception) {
                _message.value = "Eroare: ${e.message}"
            }
        }
    }

    fun respondToWar(warId: String, accept: Boolean, reason: String = "") {
        viewModelScope.launch {
            try {
                val result = repository.respondToWar(warId, accept, reason)
                _message.value = if (result.isSuccess) {
                    if (accept) "✅ War acceptat! Luptă!" else "❌ War refuzat."
                } else "Eroare!"
                if (result.isSuccess) loadWars("")
            } catch (e: Exception) {
                _message.value = "Eroare: ${e.message}"
            }
        }
    }
}