package com.example.paceup.model

data class ClanWar(
    val id: String = "",
    val challengerClanId: String = "",
    val challengerClanName: String = "",
    val challengedClanId: String = "",
    val challengedClanName: String = "",
    val challengerXp: Int = 0,
    val challengedXp: Int = 0,
    val durationDays: Int = 7,
    val startTime: Long = 0L,
    val endTime: Long = 0L,
    val status: String = "pending",
    val declineReason: String = ""
)