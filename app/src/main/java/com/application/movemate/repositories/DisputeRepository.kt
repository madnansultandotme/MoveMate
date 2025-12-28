package com.application.movemate.repositories

import com.application.movemate.models.Dispute
import com.application.movemate.models.DisputeStatus
import com.application.movemate.models.DisputePriority
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DisputeRepository {

    private val db = Firebase.firestore
    private val disputesCollection = db.collection("disputes")

    // Create dispute
    suspend fun createDispute(dispute: Dispute): Result<String> {
        return try {
            val documentReference = disputesCollection.add(dispute).await()
            Result.success(documentReference.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get dispute by ID
    suspend fun getDispute(disputeId: String): Result<Dispute?> {
        return try {
            val document = disputesCollection.document(disputeId).get().await()
            val dispute = document.toObject(Dispute::class.java)?.copy(id = document.id)
            Result.success(dispute)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update dispute
    suspend fun updateDispute(disputeId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            val updatesWithTimestamp = updates.toMutableMap()
            updatesWithTimestamp["updatedAt"] = System.currentTimeMillis()
            disputesCollection.document(disputeId).update(updatesWithTimestamp).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get all disputes (for Admin)
    fun getAllDisputes(): Flow<List<Dispute>> = flow {
        try {
            val snapshot = disputesCollection
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            val disputes = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Dispute::class.java)?.copy(id = doc.id)
            }
            emit(disputes)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Get disputes by status
    fun getDisputesByStatus(status: DisputeStatus): Flow<List<Dispute>> = flow {
        try {
            val snapshot = disputesCollection
                .whereEqualTo("status", status.name)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            val disputes = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Dispute::class.java)?.copy(id = doc.id)
            }
            emit(disputes)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Get open disputes (pending review)
    fun getOpenDisputes(): Flow<List<Dispute>> = flow {
        try {
            val snapshot = disputesCollection
                .whereIn("status", listOf(DisputeStatus.OPEN.name, DisputeStatus.IN_REVIEW.name))
                .orderBy("priority", Query.Direction.DESCENDING)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .get()
                .await()
            val disputes = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Dispute::class.java)?.copy(id = doc.id)
            }
            emit(disputes)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Get high priority disputes
    fun getHighPriorityDisputes(): Flow<List<Dispute>> = flow {
        try {
            val snapshot = disputesCollection
                .whereIn("priority", listOf(DisputePriority.HIGH.name, DisputePriority.URGENT.name))
                .whereIn("status", listOf(DisputeStatus.OPEN.name, DisputeStatus.IN_REVIEW.name))
                .get()
                .await()
            val disputes = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Dispute::class.java)?.copy(id = doc.id)
            }
            emit(disputes)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Get disputes by user
    fun getDisputesByUser(userId: String): Flow<List<Dispute>> = flow {
        try {
            val snapshot = disputesCollection
                .whereEqualTo("raisedBy", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            val disputes = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Dispute::class.java)?.copy(id = doc.id)
            }
            emit(disputes)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Get disputes against a user
    fun getDisputesAgainstUser(userId: String): Flow<List<Dispute>> = flow {
        try {
            val snapshot = disputesCollection
                .whereEqualTo("againstUserId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            val disputes = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Dispute::class.java)?.copy(id = doc.id)
            }
            emit(disputes)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Get disputes for a shipment
    suspend fun getDisputeForShipment(shipmentId: String): Result<Dispute?> {
        return try {
            val snapshot = disputesCollection
                .whereEqualTo("shipmentId", shipmentId)
                .limit(1)
                .get()
                .await()
            val dispute = snapshot.documents.firstOrNull()?.let { doc ->
                doc.toObject(Dispute::class.java)?.copy(id = doc.id)
            }
            Result.success(dispute)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update dispute status
    suspend fun updateDisputeStatus(disputeId: String, status: DisputeStatus): Result<Unit> {
        return try {
            val updates = mutableMapOf<String, Any>(
                "status" to status.name,
                "updatedAt" to System.currentTimeMillis()
            )
            if (status == DisputeStatus.RESOLVED || status == DisputeStatus.CLOSED) {
                updates["resolvedAt"] = System.currentTimeMillis()
            }
            disputesCollection.document(disputeId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Assign dispute to admin
    suspend fun assignDisputeToAdmin(disputeId: String, adminId: String, adminName: String): Result<Unit> {
        return try {
            disputesCollection.document(disputeId).update(
                mapOf(
                    "assignedAdminId" to adminId,
                    "assignedAdminName" to adminName,
                    "status" to DisputeStatus.IN_REVIEW.name,
                    "updatedAt" to System.currentTimeMillis()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Resolve dispute
    suspend fun resolveDispute(
        disputeId: String,
        resolution: String,
        notes: String? = null
    ): Result<Unit> {
        return try {
            val updates = mutableMapOf<String, Any>(
                "status" to DisputeStatus.RESOLVED.name,
                "resolution" to resolution,
                "resolvedAt" to System.currentTimeMillis(),
                "updatedAt" to System.currentTimeMillis()
            )
            notes?.let { updates["resolutionNotes"] = it }
            disputesCollection.document(disputeId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Add evidence to dispute
    suspend fun addEvidence(disputeId: String, evidenceUrl: String): Result<Unit> {
        return try {
            val disputeDoc = disputesCollection.document(disputeId).get().await()
            val dispute = disputeDoc.toObject(Dispute::class.java)
            dispute?.let {
                val updatedEvidence = it.evidence + evidenceUrl
                disputesCollection.document(disputeId).update(
                    mapOf(
                        "evidence" to updatedEvidence,
                        "updatedAt" to System.currentTimeMillis()
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update dispute priority
    suspend fun updateDisputePriority(disputeId: String, priority: DisputePriority): Result<Unit> {
        return try {
            disputesCollection.document(disputeId).update(
                mapOf(
                    "priority" to priority.name,
                    "updatedAt" to System.currentTimeMillis()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get dispute stats (for Admin Dashboard)
    suspend fun getDisputeStats(): Result<DisputeStats> {
        return try {
            val allDisputes = disputesCollection.get().await()

            var open = 0
            var inReview = 0
            var resolved = 0
            var urgent = 0

            allDisputes.documents.forEach { doc ->
                val dispute = doc.toObject(Dispute::class.java)
                dispute?.let {
                    when (it.status) {
                        DisputeStatus.OPEN -> open++
                        DisputeStatus.IN_REVIEW -> inReview++
                        DisputeStatus.RESOLVED, DisputeStatus.CLOSED -> resolved++
                    }
                    if (it.priority == DisputePriority.URGENT || it.priority == DisputePriority.HIGH) {
                        urgent++
                    }
                }
            }

            Result.success(DisputeStats(
                totalDisputes = allDisputes.size(),
                openDisputes = open,
                inReviewDisputes = inReview,
                resolvedDisputes = resolved,
                urgentDisputes = urgent
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

data class DisputeStats(
    val totalDisputes: Int = 0,
    val openDisputes: Int = 0,
    val inReviewDisputes: Int = 0,
    val resolvedDisputes: Int = 0,
    val urgentDisputes: Int = 0
)

