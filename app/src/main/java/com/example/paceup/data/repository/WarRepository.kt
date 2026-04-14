package com.example.paceup.data.repository

import com.example.paceup.model.ClanWar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class WarRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun sendWarChallenge(
        myClanId: String,
        myClanName: String,
        targetClanId: String,
        targetClanName: String,
        durationDays: Int
    ): Result<Unit> {
        return try {
            val warRef = firestore.collection("wars").document()
            val now = System.currentTimeMillis()
            val war = hashMapOf(
                "id" to warRef.id,
                "challengerClanId" to myClanId,
                "challengerClanName" to myClanName,
                "challengedClanId" to targetClanId,
                "challengedClanName" to targetClanName,
                "challengerXp" to 0,
                "challengedXp" to 0,
                "durationDays" to durationDays,
                "startTime" to now,
                "endTime" to (now + durationDays * 24 * 60 * 60 * 1000L),
                "status" to "pending",
                "declineReason" to ""
            )
            warRef.set(war).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPendingWars(clanId: String): Result<List<ClanWar>> {
        return try {
            val snapshot = firestore.collection("wars")
                .whereEqualTo("challengedClanId", clanId)
                .get().await()
            val wars = snapshot.toObjects(ClanWar::class.java)
                .filter { it.status == "pending" }
            Result.success(wars)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getActiveWar(clanId: String): Result<ClanWar?> {
        return try {
            val asChallenger = firestore.collection("wars")
                .whereEqualTo("challengerClanId", clanId)
                .get().await()
                .toObjects(ClanWar::class.java)
                .firstOrNull { it.status == "active" }

            if (asChallenger != null) return Result.success(asChallenger)

            val asChallenged = firestore.collection("wars")
                .whereEqualTo("challengedClanId", clanId)
                .get().await()
                .toObjects(ClanWar::class.java)
                .firstOrNull { it.status == "active" }

            Result.success(asChallenged)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun respondToWar(warId: String, accept: Boolean, reason: String = ""): Result<Unit> {
        return try {
            val updates = if (accept) {
                mapOf("status" to "active")
            } else {
                mapOf("status" to "declined", "declineReason" to reason)
            }
            firestore.collection("wars").document(warId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}