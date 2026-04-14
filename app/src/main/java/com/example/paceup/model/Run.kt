package com.example.paceup.model

data class Run(
    val id: String = "",
    val uid: String = "",
    val distanceKm: Double = 0.0,
    val xpEarned: Int = 0,
    val durationSeconds: Int = 0,
    val avgPaceSecondsPerKm: Int = 0,
    val timestamp: Long = 0L
)