package com.example.paceup.data.repository

import com.example.paceup.model.Clan
import com.example.paceup.model.ClanMember
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ClanRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun createClan(name: String, description: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("Neautentificat")
            val userDoc = firestore.collection("users").document(uid).get().await()
            val userXp = userDoc.getLong("xp")?.toInt() ?: 0

            val clanRef = firestore.collection("clans").document()
            val clan = hashMapOf(
                "id" to clanRef.id,
                "name" to name,
                "description" to description,
                "totalXp" to userXp,
                "memberCount" to 1,
                "maxMembers" to 20,
                "leaderId" to uid
            )
            clanRef.set(clan).await()
            firestore.collection("users").document(uid)
                .update("clanId", clanRef.id, "clanRole", "Capitan").await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun joinClan(clanId: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("Neautentificat")
            val userDoc = firestore.collection("users").document(uid).get().await()
            val userXp = userDoc.getLong("xp")?.toInt() ?: 0

            val clanRef = firestore.collection("clans").document(clanId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(clanRef)
                val currentCount = snapshot.getLong("memberCount")?.toInt() ?: 0
                val maxMembers = snapshot.getLong("maxMembers")?.toInt() ?: 20
                val currentXp = snapshot.getLong("totalXp")?.toInt() ?: 0
                if (currentCount >= maxMembers) throw Exception("Clanul e plin!")
                transaction.update(clanRef, mapOf(
                    "memberCount" to currentCount + 1,
                    "totalXp" to currentXp + userXp
                ))
            }.await()
            firestore.collection("users").document(uid)
                .update("clanId", clanId, "clanRole", "Membru").await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun leaveClan(clanId: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("Neautentificat")
            val userDoc = firestore.collection("users").document(uid).get().await()
            val userXp = userDoc.getLong("xp")?.toInt() ?: 0

            val clanRef = firestore.collection("clans").document(clanId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(clanRef)
                val currentCount = snapshot.getLong("memberCount")?.toInt() ?: 1
                val currentXp = snapshot.getLong("totalXp")?.toInt() ?: 0
                transaction.update(clanRef, mapOf(
                    "memberCount" to maxOf(0, currentCount - 1),
                    "totalXp" to maxOf(0, currentXp - userXp)
                ))
            }.await()
            firestore.collection("users").document(uid)
                .update("clanId", "", "clanRole", "").await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteClan(clanId: String): Result<Unit> {
        return try {
            // Resetam toti membrii
            val membersSnapshot = firestore.collection("users")
                .whereEqualTo("clanId", clanId)
                .get().await()
            val batch = firestore.batch()
            membersSnapshot.documents.forEach { doc ->
                batch.update(doc.reference, mapOf("clanId" to "", "clanRole" to ""))
            }
            batch.delete(firestore.collection("clans").document(clanId))
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateMemberRole(targetUid: String, newRole: String, clanId: String): Result<Unit> {
        return try {
            // Daca promovam la Vicecapitan, demoteam vechiul vice la Veteran
            if (newRole == "Vicecapitan") {
                val currentVice = firestore.collection("users")
                    .whereEqualTo("clanId", clanId)
                    .whereEqualTo("clanRole", "Vicecapitan")
                    .get().await()
                val batch = firestore.batch()
                currentVice.documents.forEach { doc ->
                    batch.update(doc.reference, "clanRole", "Veteran")
                }
                batch.update(
                    firestore.collection("users").document(targetUid),
                    "clanRole", newRole
                )
                batch.commit().await()
            } else {
                firestore.collection("users").document(targetUid)
                    .update("clanRole", newRole).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getClans(): Result<List<Clan>> {
        return try {
            val snapshot = firestore.collection("clans")
                .orderBy("totalXp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get().await()
            val clans = snapshot.toObjects(Clan::class.java)
            Result.success(clans)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserClan(clanId: String): Result<Clan> {
        return try {
            val doc = firestore.collection("clans").document(clanId).get().await()
            val clan = doc.toObject(Clan::class.java) ?: throw Exception("Clan negasit")
            Result.success(clan)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getClanMembers(clanId: String): Result<List<ClanMember>> {
        return try {
            val snapshot = firestore.collection("users")
                .whereEqualTo("clanId", clanId)
                .get().await()
            val members = snapshot.documents.map { doc ->
                ClanMember(
                    uid = doc.getString("uid") ?: "",
                    username = doc.getString("username") ?: "",
                    xp = doc.getLong("xp")?.toInt() ?: 0,
                    level = doc.getLong("level")?.toInt() ?: 1,
                    role = doc.getString("clanRole") ?: "Membru"
                )
            }.sortedByDescending { it.xp }
            Result.success(members)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun recalculateClanXp(clanId: String): Result<Unit> {
        return try {
            val membersSnapshot = firestore.collection("users")
                .whereEqualTo("clanId", clanId)
                .get().await()
            val totalXp = membersSnapshot.documents.sumOf {
                it.getLong("xp")?.toInt() ?: 0
            }
            firestore.collection("clans").document(clanId)
                .update("totalXp", totalXp).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}