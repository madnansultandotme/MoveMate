package com.application.movemate.repositories

import com.application.movemate.models.Earnings
import com.application.movemate.models.EarningsSummary
import com.application.movemate.models.DailyEarning
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class EarningsRepository {

    private val db = Firebase.firestore
    private val earningsCollection = db.collection("earnings")
    private val transactionsCollection = db.collection("transactions")

    // Get carrier earnings
    suspend fun getCarrierEarnings(carrierId: String): Result<Earnings?> {
        return try {
            val document = earningsCollection.document(carrierId).get().await()
            val earnings = document.toObject(Earnings::class.java)?.copy(carrierId = carrierId)
            Result.success(earnings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Create or update carrier earnings
    suspend fun updateCarrierEarnings(carrierId: String, earnings: Earnings): Result<Unit> {
        return try {
            earningsCollection.document(carrierId).set(earnings).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Add earnings (after completing a delivery)
    suspend fun addEarnings(carrierId: String, amount: Double): Result<Unit> {
        return try {
            val document = earningsCollection.document(carrierId).get().await()
            val currentEarnings = document.toObject(Earnings::class.java) ?: Earnings(carrierId = carrierId)

            val updatedEarnings = currentEarnings.copy(
                totalEarnings = currentEarnings.totalEarnings + amount,
                pendingEarnings = currentEarnings.pendingEarnings + amount,
                lifetimeEarnings = currentEarnings.lifetimeEarnings + amount,
                totalDeliveries = currentEarnings.totalDeliveries + 1,
                averagePerTrip = (currentEarnings.lifetimeEarnings + amount) / (currentEarnings.totalDeliveries + 1),
                lastUpdated = System.currentTimeMillis()
            )

            earningsCollection.document(carrierId).set(updatedEarnings).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Move pending to available (after payment confirmation)
    suspend fun confirmPendingEarnings(carrierId: String, amount: Double): Result<Unit> {
        return try {
            val document = earningsCollection.document(carrierId).get().await()
            val currentEarnings = document.toObject(Earnings::class.java) ?: return Result.failure(Exception("No earnings found"))

            val updatedEarnings = currentEarnings.copy(
                pendingEarnings = (currentEarnings.pendingEarnings - amount).coerceAtLeast(0.0),
                availableBalance = currentEarnings.availableBalance + amount,
                lastUpdated = System.currentTimeMillis()
            )

            earningsCollection.document(carrierId).set(updatedEarnings).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Deduct from available balance (after payout)
    suspend fun deductFromBalance(carrierId: String, amount: Double): Result<Unit> {
        return try {
            val document = earningsCollection.document(carrierId).get().await()
            val currentEarnings = document.toObject(Earnings::class.java) ?: return Result.failure(Exception("No earnings found"))

            if (currentEarnings.availableBalance < amount) {
                return Result.failure(Exception("Insufficient balance"))
            }

            val updatedEarnings = currentEarnings.copy(
                availableBalance = currentEarnings.availableBalance - amount,
                lastUpdated = System.currentTimeMillis()
            )

            earningsCollection.document(carrierId).set(updatedEarnings).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get earnings summary for a period
    suspend fun getEarningsSummary(
        carrierId: String,
        period: String, // "daily", "weekly", "monthly"
        startDate: Long,
        endDate: Long
    ): Result<EarningsSummary> {
        return try {
            // Get completed transactions for the carrier in the date range
            val transactions = transactionsCollection
                .whereEqualTo("userId", carrierId)
                .whereEqualTo("type", "PAYOUT")
                .whereEqualTo("status", "COMPLETED")
                .whereGreaterThanOrEqualTo("createdAt", startDate)
                .whereLessThanOrEqualTo("createdAt", endDate)
                .get()
                .await()

            var totalAmount = 0.0
            var deliveryCount = 0
            val dailyBreakdownMap = mutableMapOf<Long, Pair<Double, Int>>()

            transactions.documents.forEach { doc ->
                val amount = doc.getDouble("amount") ?: 0.0
                val createdAt = doc.getLong("createdAt") ?: 0

                totalAmount += amount
                deliveryCount++

                // Group by day
                val dayTimestamp = (createdAt / (24 * 60 * 60 * 1000)) * (24 * 60 * 60 * 1000)
                val existing = dailyBreakdownMap[dayTimestamp] ?: Pair(0.0, 0)
                dailyBreakdownMap[dayTimestamp] = Pair(existing.first + amount, existing.second + 1)
            }

            val dailyBreakdown = dailyBreakdownMap.map { (date, data) ->
                DailyEarning(
                    date = date,
                    amount = data.first,
                    deliveryCount = data.second
                )
            }.sortedBy { it.date }

            Result.success(EarningsSummary(
                period = period,
                startDate = startDate,
                endDate = endDate,
                totalAmount = totalAmount,
                deliveryCount = deliveryCount,
                percentageChange = 0.0, // Would need previous period data to calculate
                dailyBreakdown = dailyBreakdown
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get weekly earnings for chart
    suspend fun getWeeklyEarnings(carrierId: String): Result<List<DailyEarning>> {
        val endDate = System.currentTimeMillis()
        val startDate = endDate - (7 * 24 * 60 * 60 * 1000) // 7 days ago

        return try {
            val transactions = transactionsCollection
                .whereEqualTo("userId", carrierId)
                .whereEqualTo("type", "PAYOUT")
                .whereEqualTo("status", "COMPLETED")
                .whereGreaterThanOrEqualTo("createdAt", startDate)
                .whereLessThanOrEqualTo("createdAt", endDate)
                .get()
                .await()

            val dailyMap = mutableMapOf<Long, Pair<Double, Int>>()

            // Initialize all 7 days
            for (i in 0..6) {
                val dayTimestamp = ((startDate / (24 * 60 * 60 * 1000)) + i) * (24 * 60 * 60 * 1000)
                dailyMap[dayTimestamp] = Pair(0.0, 0)
            }

            transactions.documents.forEach { doc ->
                val amount = doc.getDouble("amount") ?: 0.0
                val createdAt = doc.getLong("createdAt") ?: 0
                val dayTimestamp = (createdAt / (24 * 60 * 60 * 1000)) * (24 * 60 * 60 * 1000)

                val existing = dailyMap[dayTimestamp] ?: Pair(0.0, 0)
                dailyMap[dayTimestamp] = Pair(existing.first + amount, existing.second + 1)
            }

            val dailyEarnings = dailyMap.map { (date, data) ->
                DailyEarning(
                    date = date,
                    amount = data.first,
                    deliveryCount = data.second
                )
            }.sortedBy { it.date }

            Result.success(dailyEarnings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get monthly earnings for chart
    suspend fun getMonthlyEarnings(carrierId: String): Result<List<DailyEarning>> {
        val endDate = System.currentTimeMillis()
        val startDate = endDate - (30L * 24 * 60 * 60 * 1000) // 30 days ago

        return try {
            val transactions = transactionsCollection
                .whereEqualTo("userId", carrierId)
                .whereEqualTo("type", "PAYOUT")
                .whereEqualTo("status", "COMPLETED")
                .whereGreaterThanOrEqualTo("createdAt", startDate)
                .whereLessThanOrEqualTo("createdAt", endDate)
                .get()
                .await()

            val dailyMap = mutableMapOf<Long, Pair<Double, Int>>()

            transactions.documents.forEach { doc ->
                val amount = doc.getDouble("amount") ?: 0.0
                val createdAt = doc.getLong("createdAt") ?: 0
                val dayTimestamp = (createdAt / (24 * 60 * 60 * 1000)) * (24 * 60 * 60 * 1000)

                val existing = dailyMap[dayTimestamp] ?: Pair(0.0, 0)
                dailyMap[dayTimestamp] = Pair(existing.first + amount, existing.second + 1)
            }

            val dailyEarnings = dailyMap.map { (date, data) ->
                DailyEarning(
                    date = date,
                    amount = data.first,
                    deliveryCount = data.second
                )
            }.sortedBy { it.date }

            Result.success(dailyEarnings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

