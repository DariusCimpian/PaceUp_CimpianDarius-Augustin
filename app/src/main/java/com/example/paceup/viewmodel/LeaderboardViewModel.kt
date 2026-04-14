package com.example.paceup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paceup.data.repository.LeaderboardRepository
import com.example.paceup.model.Clan
import com.example.paceup.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LeaderboardViewModel : ViewModel() {
    private val repository = LeaderboardRepository()

    private val _topUsers = MutableStateFlow<List<User>>(emptyList())
    val topUsers: StateFlow<List<User>> = _topUsers

    private val _topClans = MutableStateFlow<List<Clan>>(emptyList())
    val topClans: StateFlow<List<Clan>> = _topClans

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init { loadData() }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            val usersResult = repository.getTopUsers()
            if (usersResult.isSuccess) _topUsers.value = usersResult.getOrNull() ?: emptyList()
            val clansResult = repository.getTopClans()
            if (clansResult.isSuccess) _topClans.value = clansResult.getOrNull() ?: emptyList()
            _isLoading.value = false
        }
    }
}