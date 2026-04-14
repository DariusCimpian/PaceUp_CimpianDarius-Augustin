package com.example.paceup.model

data class ChatMessage(
    val id: String = "",
    val uid: String = "",
    val username: String = "",
    val message: String = "",
    val timestamp: Long = 0L
)