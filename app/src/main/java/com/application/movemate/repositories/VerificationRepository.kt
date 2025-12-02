package com.application.movemate.repositories

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class VerificationRepository {

    private val db = Firebase.firestore

    suspend fun uploadDocumentUrl(carrierId: String, documentName: String, documentUrl: String): Result<Unit> {
        return try {
            val documentData = mapOf(
                "name" to documentName,
                "url" to documentUrl,
                "status" to "Pending"
            )
            db.collection("carriers").document(carrierId)
                .update("documents", FieldValue.arrayUnion(documentData))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

