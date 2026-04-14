package com.example.paceup.model

data class User(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val level: Int = 1,
    val xp: Int = 0,
    val totalKm: Double = 0.0,
    val clanId: String = ""
)