package com.example.paceup.data.repository

import com.example.paceup.model.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ChatRepository {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance("https://paceup-c3ac3-default-rtdb.europe-west1.firebasedatabase.app")
    private val firestore = FirebaseFirestore.getInstance()

    fun getMessages(chatPath: String): Flow<List<ChatMessage>> = callbackFlow {
        val ref = database.getReference(chatPath).limitToLast(50)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull {
                    it.getValue(ChatMessage::class.java)
                }
                trySend(messages)
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun sendMessage(chatPath: String, message: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("Neautentificat")
            val userDoc = firestore.collection("users").document(uid).get().await()
            val username = userDoc.getString("username") ?: "Anonim"
            val ref = database.getReference(chatPath).push()
            val chatMessage = ChatMessage(
                id = ref.key ?: "",
                uid = uid,
                username = username,
                message = message,
                timestamp = System.currentTimeMillis()
            )
            ref.setValue(chatMessage).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}