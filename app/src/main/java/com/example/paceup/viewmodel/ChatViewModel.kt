package com.example.paceup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paceup.data.repository.ChatRepository
import com.example.paceup.model.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val repository = ChatRepository()
    val currentUid: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    fun listenToChat(chatPath: String) {
        viewModelScope.launch {
            repository.getMessages(chatPath).collect {
                _messages.value = it
            }
        }
    }

    fun sendMessage(chatPath: String, message: String) {
        viewModelScope.launch {
            repository.sendMessage(chatPath, message)
        }
    }
}