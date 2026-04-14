package com.example.paceup.model

data class Clan(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val totalXp: Int = 0,
    val memberCount: Int = 0,
    val maxMembers: Int = 20,
    val leaderId: String = ""
)