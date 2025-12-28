package com.application.movemate.repositories

import com.application.movemate.models.Payout
import com.application.movemate.models.PayoutStatus
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PayoutRepository {

    private val db = Firebase.firestore
    private val payoutsCollection = db.collection("payouts")

    // Request payout
    suspend fun requestPayout(payout: Payout): Result<String> {
        return try {
            val documentReference = payoutsCollection.add(payout).await()
            Result.success(documentReference.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get payout by ID
    suspend fun getPayout(payoutId: String): Result<Payout?> {
        return try {
            val document = payoutsCollection.document(payoutId).get().await()
            val payout = document.toObject(Payout::class.java)?.copy(id = document.id)
            Result.success(payout)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update payout
    suspend fun updatePayout(payoutId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            payoutsCollection.document(payoutId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update payout status
    suspend fun updatePayoutStatus(payoutId: String, status: PayoutStatus, reason: String? = null): Result<Unit> {
        return try {
            val updates = mutableMapOf<String, Any>(
                "status" to status.name
            )
            when (status) {
                PayoutStatus.COMPLETED -> updates["processedAt"] = System.currentTimeMillis()
                PayoutStatus.FAILED -> reason?.let { updates["failureReason"] = it }
                else -> {}
            }
            payoutsCollection.document(payoutId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get all payouts (for Admin)
    fun getAllPayouts(): Flow<List<Payout>> = flow {
        try {
            val snapshot = payoutsCollection
                .orderBy("requestedAt", Query.Direction.DESCENDING)
                .get()
                .await()
            val payouts = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Payout::class.java)?.copy(id = doc.id)
            }
            emit(payouts)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Get payouts by carrier
    fun getPayoutsByCarrier(carrierId: String): Flow<List<Payout>> = flow {
        try {
            val snapshot = payoutsCollection
                .whereEqualTo("carrierId", carrierId)
                .orderBy("requestedAt", Query.Direction.DESCENDING)
                .get()
                .await()
            val payouts = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Payout::class.java)?.copy(id = doc.id)
            }
            emit(payouts)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Get payouts by status
    fun getPayoutsByStatus(status: PayoutStatus): Flow<List<Payout>> = flow {
        try {
            val snapshot = payoutsCollection
                .whereEqualTo("status", status.name)
                .orderBy("requestedAt", Query.Direction.DESCENDING)
                .get()
                .await()
            val payouts = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Payout::class.java)?.copy(id = doc.id)
            }
            emit(payouts)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Get pending payouts
    fun getPendingPayouts(): Flow<List<Payout>> = flow {
        try {
            val snapshot = payoutsCollection
                .whereIn("status", listOf(PayoutStatus.PENDING.name, PayoutStatus.PROCESSING.name))
                .orderBy("requestedAt", Query.Direction.ASCENDING)
                .get()
                .await()
            val payouts = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Payout::class.java)?.copy(id = doc.id)
            }
            emit(payouts)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Get recent payouts for carrier
    fun getRecentPayoutsForCarrier(carrierId: String, limit: Int = 5): Flow<List<Payout>> = flow {
        try {
            val snapshot = payoutsCollection
                .whereEqualTo("carrierId", carrierId)
                .orderBy("requestedAt", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()
            val payouts = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Payout::class.java)?.copy(id = doc.id)
            }
            emit(payouts)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Process payout (mark as processing)
    suspend fun processPayout(payoutId: String): Result<Unit> {
        return try {
            payoutsCollection.document(payoutId).update(
                "status", PayoutStatus.PROCESSING.name
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Complete payout
    suspend fun completePayout(payoutId: String, referenceNumber: String): Result<Unit> {
        return try {
            payoutsCollection.document(payoutId).update(
                mapOf(
                    "status" to PayoutStatus.COMPLETED.name,
                    "processedAt" to System.currentTimeMillis(),
                    "referenceNumber" to referenceNumber
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Fail payout
    suspend fun failPayout(payoutId: String, reason: String): Result<Unit> {
        return try {
            payoutsCollection.document(payoutId).update(
                mapOf(
                    "status" to PayoutStatus.FAILED.name,
                    "failureReason" to reason,
                    "processedAt" to System.currentTimeMillis()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Cancel payout
    suspend fun cancelPayout(payoutId: String): Result<Unit> {
        return try {
            payoutsCollection.document(payoutId).update(
                "status", PayoutStatus.CANCELLED.name
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get payout stats
    suspend fun getPayoutStats(): Result<PayoutStats> {
        return try {
            val allPayouts = payoutsCollection.get().await()

            var pending = 0
            var pendingAmount = 0.0
            var processing = 0
            var processingAmount = 0.0
            var completed = 0
            var completedAmount = 0.0

            allPayouts.documents.forEach { doc ->
                val payout = doc.toObject(Payout::class.java)
                payout?.let {
                    when (it.status) {
                        PayoutStatus.PENDING -> {
                            pending++
                            pendingAmount += it.amount
                        }
                        PayoutStatus.PROCESSING -> {
                            processing++
                            processingAmount += it.amount
                        }
                        PayoutStatus.COMPLETED -> {
                            completed++
                            completedAmount += it.amount
                        }
                        else -> {}
                    }
                }
            }

            Result.success(PayoutStats(
                pendingCount = pending,
                pendingAmount = pendingAmount,
                processingCount = processing,
                processingAmount = processingAmount,
                completedCount = completed,
                completedAmount = completedAmount
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get carrier payout stats
    suspend fun getCarrierPayoutStats(carrierId: String): Result<CarrierPayoutStats> {
        return try {
            val payouts = payoutsCollection
                .whereEqualTo("carrierId", carrierId)
                .get()
                .await()

            var totalWithdrawn = 0.0
            var pendingWithdrawal = 0.0
            var lastPayoutDate: Long? = null

            payouts.documents.forEach { doc ->
                val payout = doc.toObject(Payout::class.java)
                payout?.let {
                    when (it.status) {
                        PayoutStatus.COMPLETED -> {
                            totalWithdrawn += it.amount
                            if (lastPayoutDate == null || (it.processedAt ?: 0) > (lastPayoutDate ?: 0)) {
                                lastPayoutDate = it.processedAt
                            }
                        }
                        PayoutStatus.PENDING, PayoutStatus.PROCESSING -> {
                            pendingWithdrawal += it.amount
                        }
                        else -> {}
                    }
                }
            }

            Result.success(CarrierPayoutStats(
                totalWithdrawn = totalWithdrawn,
                pendingWithdrawal = pendingWithdrawal,
                totalPayouts = payouts.size(),
                lastPayoutDate = lastPayoutDate
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

data class PayoutStats(
    val pendingCount: Int = 0,
    val pendingAmount: Double = 0.0,
    val processingCount: Int = 0,
    val processingAmount: Double = 0.0,
    val completedCount: Int = 0,
    val completedAmount: Double = 0.0
)

data class CarrierPayoutStats(
    val totalWithdrawn: Double = 0.0,
    val pendingWithdrawal: Double = 0.0,
    val totalPayouts: Int = 0,
    val lastPayoutDate: Long? = null
)

