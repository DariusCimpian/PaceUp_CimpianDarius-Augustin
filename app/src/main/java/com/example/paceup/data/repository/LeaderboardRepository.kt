package com.example.paceup.data.repository

import com.example.paceup.model.Clan
import com.example.paceup.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class LeaderboardRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getTopUsers(): Result<List<User>> {
        return try {
            val snapshot = firestore.collection("users")
                .orderBy("xp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(20)
                .get().await()
            val users = snapshot.toObjects(User::class.java)
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTopClans(): Result<List<Clan>> {
        return try {
            val snapshot = firestore.collection("clans")
                .orderBy("totalXp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(20)
                .get().await()
            val clans = snapshot.toObjects(Clan::class.java)
            Result.success(clans)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}