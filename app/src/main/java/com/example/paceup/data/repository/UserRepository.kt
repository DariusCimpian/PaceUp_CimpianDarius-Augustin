package com.example.paceup.data.repository

import com.example.paceup.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getCurrentUser(): Result<User> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("User neautentificat")
            val doc = firestore.collection("users").document(uid).get().await()
            val user = doc.toObject(User::class.java) ?: throw Exception("User negasit")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}