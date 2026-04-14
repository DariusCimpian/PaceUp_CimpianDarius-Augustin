package com.example.paceup.data.repository

import com.example.paceup.model.Run
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RunRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun saveRun(
        distanceKm: Double,
        xpEarned: Int,
        durationSeconds: Int
    ): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("Neautentificat")

            val avgPace = if (distanceKm > 0) (durationSeconds / distanceKm).toInt() else 0

            val run = hashMapOf(
                "uid" to uid,
                "distanceKm" to distanceKm,
                "xpEarned" to xpEarned,
                "durationSeconds" to durationSeconds,
                "avgPaceSecondsPerKm" to avgPace,
                "timestamp" to System.currentTimeMillis()
            )
            firestore.collection("runs").add(run).await()

            // Actualizeaza user
            val userRef = firestore.collection("users").document(uid)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)
                val currentXp = snapshot.getLong("xp")?.toInt() ?: 0
                val currentKm = snapshot.getDouble("totalKm") ?: 0.0
                val currentLevel = snapshot.getLong("level")?.toInt() ?: 1
                val newXp = currentXp + xpEarned
                val newLevel = if (newXp >= currentLevel * 500) currentLevel + 1 else currentLevel
                transaction.update(userRef, mapOf(
                    "xp" to newXp,
                    "totalKm" to currentKm + distanceKm,
                    "level" to newLevel
                ))
            }.await()

            // Actualizeaza XP clan daca e cazul
            val userDoc = firestore.collection("users").document(uid).get().await()
            val clanId = userDoc.getString("clanId") ?: ""
            if (clanId.isNotEmpty()) {
                val clanRef = firestore.collection("clans").document(clanId)
                firestore.runTransaction { transaction ->
                    val clanSnap = transaction.get(clanRef)
                    val currentClanXp = clanSnap.getLong("totalXp")?.toInt() ?: 0
                    transaction.update(clanRef, "totalXp", currentClanXp + xpEarned)
                }.await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserRuns(): Result<List<Run>> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("Neautentificat")
            val snapshot = firestore.collection("runs")
                .whereEqualTo("uid", uid)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get().await()
            val runs = snapshot.documents.map { doc ->
                Run(
                    id = doc.id,
                    uid = doc.getString("uid") ?: "",
                    distanceKm = doc.getDouble("distanceKm") ?: 0.0,
                    xpEarned = doc.getLong("xpEarned")?.toInt() ?: 0,
                    durationSeconds = doc.getLong("durationSeconds")?.toInt() ?: 0,
                    avgPaceSecondsPerKm = doc.getLong("avgPaceSecondsPerKm")?.toInt() ?: 0,
                    timestamp = doc.getLong("timestamp") ?: 0L
                )
            }
            Result.success(runs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}