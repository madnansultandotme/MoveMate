package com.application.movemate.repositories

import com.application.movemate.models.Transaction
import com.application.movemate.models.TransactionType
import com.application.movemate.models.TransactionStatus
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TransactionRepository {

    private val db = Firebase.firestore
    private val transactionsCollection = db.collection("transactions")

    // Create transaction
    suspend fun createTransaction(transaction: Transaction): Result<String> {
        return try {
            val documentReference = transactionsCollection.add(transaction).await()
            Result.success(documentReference.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get transaction by ID
    suspend fun getTransaction(transactionId: String): Result<Transaction?> {
        return try {
            val document = transactionsCollection.document(transactionId).get().await()
            val transaction = document.toObject(Transaction::class.java)?.copy(id = document.id)
            Result.success(transaction)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update transaction
    suspend fun updateTransaction(transactionId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            transactionsCollection.document(transactionId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update transaction status
    suspend fun updateTransactionStatus(transactionId: String, status: TransactionStatus): Result<Unit> {
        return try {
            val updates = mutableMapOf<String, Any>(
                "status" to status.name
            )
            if (status == TransactionStatus.COMPLETED) {
                updates["completedAt"] = System.currentTimeMillis()
            }
            transactionsCollection.document(transactionId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get all transactions (for Admin)
    fun getAllTransactions(): Flow<List<Transaction>> = flow {
        try {
            val snapshot = transactionsCollection
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            val transactions = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Transaction::class.java)?.copy(id = doc.id)
            }
            emit(transactions)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Get transactions by user
    fun getTransactionsByUser(userId: String): Flow<List<Transaction>> = flow {
        try {
            val snapshot = transactionsCollection
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            val transactions = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Transaction::class.java)?.copy(id = doc.id)
            }
            emit(transactions)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Get transactions by shipment
    fun getTransactionsByShipment(shipmentId: String): Flow<List<Transaction>> = flow {
        try {
            val snapshot = transactionsCollection
                .whereEqualTo("shipmentId", shipmentId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            val transactions = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Transaction::class.java)?.copy(id = doc.id)
            }
            emit(transactions)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Get transactions by type
    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> = flow {
        try {
            val snapshot = transactionsCollection
                .whereEqualTo("type", type.name)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            val transactions = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Transaction::class.java)?.copy(id = doc.id)
            }
            emit(transactions)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Get transactions by status
    fun getTransactionsByStatus(status: TransactionStatus): Flow<List<Transaction>> = flow {
        try {
            val snapshot = transactionsCollection
                .whereEqualTo("status", status.name)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            val transactions = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Transaction::class.java)?.copy(id = doc.id)
            }
            emit(transactions)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Get recent transactions (limited)
    fun getRecentTransactions(limit: Int = 10): Flow<List<Transaction>> = flow {
        try {
            val snapshot = transactionsCollection
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()
            val transactions = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Transaction::class.java)?.copy(id = doc.id)
            }
            emit(transactions)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Get transactions within date range
    fun getTransactionsInRange(startDate: Long, endDate: Long): Flow<List<Transaction>> = flow {
        try {
            val snapshot = transactionsCollection
                .whereGreaterThanOrEqualTo("createdAt", startDate)
                .whereLessThanOrEqualTo("createdAt", endDate)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            val transactions = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Transaction::class.java)?.copy(id = doc.id)
            }
            emit(transactions)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Get carrier payout transactions
    fun getCarrierPayouts(carrierId: String): Flow<List<Transaction>> = flow {
        try {
            val snapshot = transactionsCollection
                .whereEqualTo("userId", carrierId)
                .whereEqualTo("type", TransactionType.PAYOUT.name)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            val transactions = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Transaction::class.java)?.copy(id = doc.id)
            }
            emit(transactions)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Get loader payments
    fun getLoaderPayments(loaderId: String): Flow<List<Transaction>> = flow {
        try {
            val snapshot = transactionsCollection
                .whereEqualTo("userId", loaderId)
                .whereEqualTo("type", TransactionType.PAYMENT.name)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            val transactions = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Transaction::class.java)?.copy(id = doc.id)
            }
            emit(transactions)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Get transaction stats (for Admin Financial Dashboard)
    suspend fun getTransactionStats(): Result<TransactionStats> {
        return try {
            val allTransactions = transactionsCollection.get().await()

            var totalVolume = 0.0
            var totalRevenue = 0.0
            var pendingPayouts = 0.0
            var refunds = 0.0
            var paymentCount = 0
            var payoutCount = 0

            allTransactions.documents.forEach { doc ->
                val transaction = doc.toObject(Transaction::class.java)
                transaction?.let {
                    when (it.type) {
                        TransactionType.PAYMENT -> {
                            totalVolume += it.amount
                            paymentCount++
                        }
                        TransactionType.PAYOUT -> {
                            if (it.status == TransactionStatus.PENDING || it.status == TransactionStatus.PROCESSING) {
                                pendingPayouts += it.amount
                            }
                            payoutCount++
                        }
                        TransactionType.REFUND -> {
                            refunds += it.amount
                        }
                        TransactionType.SERVICE_FEE -> {
                            totalRevenue += it.amount
                        }
                        else -> {}
                    }
                }
            }

            Result.success(TransactionStats(
                totalVolume = totalVolume,
                totalRevenue = totalRevenue,
                pendingPayouts = pendingPayouts,
                refundsProcessed = refunds,
                totalPayments = paymentCount,
                totalPayouts = payoutCount
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get daily transaction summary for period
    suspend fun getDailyTransactionSummary(startDate: Long, endDate: Long): Result<List<DailyTransactionSummary>> {
        return try {
            val snapshot = transactionsCollection
                .whereGreaterThanOrEqualTo("createdAt", startDate)
                .whereLessThanOrEqualTo("createdAt", endDate)
                .whereEqualTo("status", TransactionStatus.COMPLETED.name)
                .get()
                .await()

            val transactions = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Transaction::class.java)
            }

            // Group by day (simplified - in production use proper date handling)
            val dailySummaries = transactions
                .groupBy { it.createdAt / (24 * 60 * 60 * 1000) }
                .map { (dayTimestamp, dayTransactions) ->
                    DailyTransactionSummary(
                        date = dayTimestamp * (24 * 60 * 60 * 1000),
                        totalAmount = dayTransactions.sumOf { it.amount },
                        transactionCount = dayTransactions.size
                    )
                }
                .sortedBy { it.date }

            Result.success(dailySummaries)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

data class TransactionStats(
    val totalVolume: Double = 0.0,
    val totalRevenue: Double = 0.0,
    val pendingPayouts: Double = 0.0,
    val refundsProcessed: Double = 0.0,
    val totalPayments: Int = 0,
    val totalPayouts: Int = 0
)

data class DailyTransactionSummary(
    val date: Long = 0,
    val totalAmount: Double = 0.0,
    val transactionCount: Int = 0
)

// Extension function for FinancialViewModel
suspend fun TransactionRepository.getFinancialStats(): Result<com.application.movemate.viewmodels.FinancialStats> {
    return try {
        val allTransactions = Firebase.firestore.collection("transactions").get().await()

        var totalVolume = 0.0
        var netRevenue = 0.0
        var pendingPayouts = 0.0
        var refundsProcessed = 0.0
        var pendingPayoutCarriers = 0
        var refundLoaders = 0
        val weeklyRevenue = mutableListOf<Double>()

        // Initialize weekly revenue for 7 days
        for (i in 0..6) {
            weeklyRevenue.add(0.0)
        }

        val carriersWithPending = mutableSetOf<String>()
        val loadersWithRefunds = mutableSetOf<String>()

        allTransactions.documents.forEach { doc ->
            val transaction = doc.toObject(Transaction::class.java)
            transaction?.let {
                when (it.type) {
                    TransactionType.PAYMENT -> {
                        totalVolume += it.amount
                    }
                    TransactionType.PAYOUT -> {
                        if (it.status == TransactionStatus.PENDING || it.status == TransactionStatus.PROCESSING) {
                            pendingPayouts += it.amount
                            carriersWithPending.add(it.userId)
                        }
                    }
                    TransactionType.REFUND -> {
                        refundsProcessed += it.amount
                        loadersWithRefunds.add(it.userId)
                    }
                    TransactionType.SERVICE_FEE -> {
                        netRevenue += it.amount
                    }
                    else -> {}
                }
            }
        }

        pendingPayoutCarriers = carriersWithPending.size
        refundLoaders = loadersWithRefunds.size

        Result.success(com.application.movemate.viewmodels.FinancialStats(
            totalVolume = totalVolume,
            netRevenue = netRevenue,
            pendingPayouts = pendingPayouts,
            refundsProcessed = refundsProcessed,
            volumeGrowthPercentage = 5.0, // Placeholder - would calculate from historical data
            revenueGrowthPercentage = 12.4, // Placeholder
            pendingPayoutCarriers = pendingPayoutCarriers,
            refundLoaders = refundLoaders,
            weeklyRevenue = weeklyRevenue
        ))
    } catch (e: Exception) {
        Result.failure(e)
    }
}

