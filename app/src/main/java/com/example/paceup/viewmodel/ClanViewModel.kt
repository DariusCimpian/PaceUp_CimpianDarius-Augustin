package com.example.paceup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paceup.data.repository.ClanRepository
import com.example.paceup.data.repository.UserRepository
import com.example.paceup.model.Clan
import com.example.paceup.model.ClanMember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClanViewModel : ViewModel() {
    private val clanRepository = ClanRepository()
    private val userRepository = UserRepository()

    private val _clans = MutableStateFlow<List<Clan>>(emptyList())
    val clans: StateFlow<List<Clan>> = _clans

    private val _userClan = MutableStateFlow<Clan?>(null)
    val userClan: StateFlow<Clan?> = _userClan

    private val _members = MutableStateFlow<List<ClanMember>>(emptyList())
    val members: StateFlow<List<ClanMember>> = _members

    private val _selectedClan = MutableStateFlow<Clan?>(null)
    val selectedClan: StateFlow<Clan?> = _selectedClan

    private val _selectedClanMembers = MutableStateFlow<List<ClanMember>>(emptyList())
    val selectedClanMembers: StateFlow<List<ClanMember>> = _selectedClanMembers

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private val _userRole = MutableStateFlow("Membru")
    val userRole: StateFlow<String> = _userRole

    private val _currentUid = MutableStateFlow("")
    val currentUid: StateFlow<String> = _currentUid

    init { loadData() }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            val userResult = userRepository.getCurrentUser()
            if (userResult.isSuccess) {
                val user = userResult.getOrNull()
                _currentUid.value = user?.uid ?: ""
                val clanId = user?.clanId ?: ""
                if (clanId.isNotEmpty()) {
                    clanRepository.recalculateClanXp(clanId)
                    val clanResult = clanRepository.getUserClan(clanId)
                    if (clanResult.isSuccess) {
                        _userClan.value = clanResult.getOrNull()
                        val membersResult = clanRepository.getClanMembers(clanId)
                        if (membersResult.isSuccess) {
                            _members.value = membersResult.getOrNull() ?: emptyList()
                        }
                        val me = _members.value.find { it.uid == user?.uid }
                        _userRole.value = me?.role ?: "Membru"
                    }
                } else {
                    _userClan.value = null
                    _members.value = emptyList()
                }
            }
            val clansResult = clanRepository.getClans()
            if (clansResult.isSuccess) _clans.value = clansResult.getOrNull() ?: emptyList()
            _isLoading.value = false
        }
    }

    fun createClan(name: String, description: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = clanRepository.createClan(name, description)
            _message.value = if (result.isSuccess) "Clan creat!" else "Eroare: ${result.exceptionOrNull()?.message}"
            loadData()
        }
    }

    fun joinClan(clanId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = clanRepository.joinClan(clanId)
            _message.value = if (result.isSuccess) "Te-ai alăturat clanului!" else "Eroare: ${result.exceptionOrNull()?.message}"
            loadData()
        }
    }

    fun leaveClan() {
        viewModelScope.launch {
            _isLoading.value = true
            val clanId = _userClan.value?.id ?: return@launch
            val result = clanRepository.leaveClan(clanId)
            _message.value = if (result.isSuccess) "Ai părăsit clanul!" else "Eroare: ${result.exceptionOrNull()?.message}"
            loadData()
        }
    }

    fun deleteClan() {
        viewModelScope.launch {
            _isLoading.value = true
            val clanId = _userClan.value?.id ?: return@launch
            val result = clanRepository.deleteClan(clanId)
            _message.value = if (result.isSuccess) "Clan șters!" else "Eroare: ${result.exceptionOrNull()?.message}"
            if (result.isSuccess) {
                _userClan.value = null
                _members.value = emptyList()
            }
            _isLoading.value = false
        }
    }

    fun updateMemberRole(targetUid: String, newRole: String) {
        viewModelScope.launch {
            val clanId = _userClan.value?.id ?: return@launch
            val result = clanRepository.updateMemberRole(targetUid, newRole, clanId)
            _message.value = if (result.isSuccess) "Rol actualizat!" else "Eroare: ${result.exceptionOrNull()?.message}"
            if (result.isSuccess) loadData()
        }
    }

    fun selectClan(clan: Clan) {
        _selectedClan.value = clan
        viewModelScope.launch {
            val result = clanRepository.getClanMembers(clan.id)
            if (result.isSuccess) _selectedClanMembers.value = result.getOrNull() ?: emptyList()
        }
    }

    fun clearSelectedClan() {
        _selectedClan.value = null
        _selectedClanMembers.value = emptyList()
    }
}