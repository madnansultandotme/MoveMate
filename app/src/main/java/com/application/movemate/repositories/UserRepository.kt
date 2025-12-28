package com.application.movemate.repositories

import com.application.movemate.models.User
import com.application.movemate.models.UserRole
import com.application.movemate.models.VerificationStatus
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserRepository {

    private val db = Firebase.firestore
    private val usersCollection = db.collection("users")

    // Create user
    suspend fun createUser(user: User): Result<String> {
        return try {
            usersCollection.document(user.id).set(user).await()
            Result.success(user.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get user by ID
    suspend fun getUser(userId: String): Result<User?> {
        return try {
            val document = usersCollection.document(userId).get().await()
            val user = document.toObject(User::class.java)?.copy(id = document.id)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update user
    suspend fun updateUser(userId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            usersCollection.document(userId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get all users (for Admin)
    fun getAllUsers(): Flow<List<User>> = flow {
        try {
            val snapshot = usersCollection.get().await()
            val users = snapshot.documents.mapNotNull { doc ->
                doc.toObject(User::class.java)?.copy(id = doc.id)
            }
            emit(users)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Get users by role
    fun getUsersByRole(role: UserRole): Flow<List<User>> = flow {
        try {
            val snapshot = usersCollection
                .whereEqualTo("role", role.name)
                .get()
                .await()
            val users = snapshot.documents.mapNotNull { doc ->
                doc.toObject(User::class.java)?.copy(id = doc.id)
            }
            emit(users)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Get pending verification users
    fun getPendingVerificationUsers(): Flow<List<User>> = flow {
        try {
            val snapshot = usersCollection
                .whereEqualTo("verificationStatus", VerificationStatus.PENDING.name)
                .get()
                .await()
            val users = snapshot.documents.mapNotNull { doc ->
                doc.toObject(User::class.java)?.copy(id = doc.id)
            }
            emit(users)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Suspend user
    suspend fun suspendUser(userId: String, reason: String): Result<Unit> {
        return try {
            val updates = mapOf(
                "isActive" to false,
                "suspendedAt" to System.currentTimeMillis(),
                "suspensionReason" to reason
            )
            usersCollection.document(userId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Unsuspend user
    suspend fun unsuspendUser(userId: String): Result<Unit> {
        return try {
            val updates = mapOf(
                "isActive" to true,
                "suspendedAt" to null,
                "suspensionReason" to null
            )
            usersCollection.document(userId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Reactivate user (alias for unsuspendUser)
    suspend fun reactivateUser(userId: String): Result<Unit> = unsuspendUser(userId)

    // Approve user verification
    suspend fun approveUser(userId: String): Result<Unit> {
        return try {
            val updates = mapOf(
                "verificationStatus" to VerificationStatus.APPROVED.name
            )
            usersCollection.document(userId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Reject user verification
    suspend fun rejectUser(userId: String, reason: String): Result<Unit> {
        return try {
            val updates = mapOf(
                "verificationStatus" to VerificationStatus.REJECTED.name,
                "rejectionReason" to reason
            )
            usersCollection.document(userId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Search users
    suspend fun searchUsers(query: String): Result<List<User>> {
        return try {
            // Note: Firestore doesn't support full-text search natively
            // This is a simple implementation that matches against name and email
            val snapshot = usersCollection.get().await()
            val users = snapshot.documents.mapNotNull { doc ->
                doc.toObject(User::class.java)?.copy(id = doc.id)
            }.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.email.contains(query, ignoreCase = true) ||
                it.id.contains(query, ignoreCase = true)
            }
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

