package com.application.movemate.repositories

import com.application.movemate.models.Loader
import com.application.movemate.models.LoaderDocuments
import com.application.movemate.models.VerificationStatus
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LoaderRepository {

    private val db = Firebase.firestore
    private val loadersCollection = db.collection("loaders")

    // Create loader profile
    suspend fun createLoader(loader: Loader): Result<String> {
        return try {
            loadersCollection.document(loader.id).set(loader).await()
            Result.success(loader.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get loader by ID
    suspend fun getLoader(loaderId: String): Result<Loader?> {
        return try {
            val document = loadersCollection.document(loaderId).get().await()
            val loader = document.toObject(Loader::class.java)?.copy(id = document.id)
            Result.success(loader)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update loader profile
    suspend fun updateLoader(loaderId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            loadersCollection.document(loaderId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get all loaders (for Admin)
    fun getAllLoaders(): Flow<List<Loader>> = flow {
        try {
            val snapshot = loadersCollection.get().await()
            val loaders = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Loader::class.java)?.copy(id = doc.id)
            }
            emit(loaders)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Get pending verification loaders
    fun getPendingVerificationLoaders(): Flow<List<Loader>> = flow {
        try {
            val snapshot = loadersCollection
                .whereEqualTo("verificationStatus", VerificationStatus.PENDING.name)
                .get()
                .await()
            val loaders = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Loader::class.java)?.copy(id = doc.id)
            }
            emit(loaders)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Update loader verification status
    suspend fun updateVerificationStatus(
        loaderId: String,
        status: VerificationStatus,
        reason: String? = null
    ): Result<Unit> {
        return try {
            val updates = mutableMapOf<String, Any>(
                "verificationStatus" to status.name,
                "updatedAt" to System.currentTimeMillis()
            )
            if (status == VerificationStatus.APPROVED) {
                updates["approvedAt"] = System.currentTimeMillis()
            } else if (status == VerificationStatus.REJECTED && reason != null) {
                updates["rejectionReason"] = reason
            }
            loadersCollection.document(loaderId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update loader documents
    suspend fun updateLoaderDocuments(loaderId: String, documents: LoaderDocuments): Result<Unit> {
        return try {
            loadersCollection.document(loaderId)
                .update("businessDocuments", documents)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update loader statistics
    suspend fun updateLoaderStats(
        loaderId: String,
        activeShipments: Int? = null,
        completedShipments: Int? = null,
        pendingBids: Int? = null,
        monthlySpend: Double? = null
    ): Result<Unit> {
        return try {
            val updates = mutableMapOf<String, Any>()
            activeShipments?.let { updates["activeShipments"] = it }
            completedShipments?.let { updates["completedShipments"] = it }
            pendingBids?.let { updates["pendingBids"] = it }
            monthlySpend?.let { updates["monthlySpend"] = it }

            if (updates.isNotEmpty()) {
                loadersCollection.document(loaderId).update(updates).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Suspend loader
    suspend fun suspendLoader(loaderId: String, reason: String): Result<Unit> {
        return try {
            val updates = mapOf(
                "isActive" to false,
                "suspendedAt" to System.currentTimeMillis(),
                "suspensionReason" to reason
            )
            loadersCollection.document(loaderId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update notification settings
    suspend fun updateNotificationSettings(
        loaderId: String,
        pushEnabled: Boolean,
        emailEnabled: Boolean
    ): Result<Unit> {
        return try {
            val updates = mapOf(
                "pushNotificationsEnabled" to pushEnabled,
                "emailNotificationsEnabled" to emailEnabled
            )
            loadersCollection.document(loaderId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

