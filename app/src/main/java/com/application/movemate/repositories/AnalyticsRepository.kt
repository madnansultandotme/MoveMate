package com.application.movemate.repositories

import com.application.movemate.models.Analytics
import com.application.movemate.models.TopRoute
import com.application.movemate.models.DailyAnalytics
import com.application.movemate.models.ShipmentStatus
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AnalyticsRepository {

    private val db = Firebase.firestore
    private val analyticsCollection = db.collection("analytics")
    private val shipmentsCollection = db.collection("shipments")
    private val loadersCollection = db.collection("loaders")
    private val carriersCollection = db.collection("carriers")
    private val transactionsCollection = db.collection("transactions")

    // Get platform analytics for a period
    suspend fun getPlatformAnalytics(
        period: String,
        startDate: Long,
        endDate: Long
    ): Result<Analytics> {
        return try {
            // Try to get cached analytics first
            val cachedDoc = analyticsCollection
                .document("${period}_${startDate}_$endDate")
                .get()
                .await()

            if (cachedDoc.exists()) {
                val analytics = cachedDoc.toObject(Analytics::class.java)
                if (analytics != null && System.currentTimeMillis() - analytics.lastUpdated < 3600000) {
                    // Return cached if less than 1 hour old
                    return Result.success(analytics)
                }
            }

            // Calculate fresh analytics
            val analytics = calculateAnalytics(period, startDate, endDate)
            
            // Cache the result
            analyticsCollection
                .document("${period}_${startDate}_$endDate")
                .set(analytics)
                .await()

            Result.success(analytics)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun calculateAnalytics(
        period: String,
        startDate: Long,
        endDate: Long
    ): Analytics {
        // Get shipment stats
        val shipmentsSnapshot = shipmentsCollection
            .whereGreaterThanOrEqualTo("createdAt", startDate)
            .whereLessThanOrEqualTo("createdAt", endDate)
            .get()
            .await()

        var totalShipments = 0
        var completedShipments = 0
        var cancelledShipments = 0
        var inTransitShipments = 0
        var delayedShipments = 0

        shipmentsSnapshot.documents.forEach { doc ->
            totalShipments++
            when (doc.getString("status")) {
                ShipmentStatus.COMPLETED.name -> completedShipments++
                ShipmentStatus.CANCELLED.name -> cancelledShipments++
                ShipmentStatus.IN_TRANSIT.name -> inTransitShipments++
                // Could add delay detection logic here
            }
        }

        // Get new loaders
        val newLoaders = loadersCollection
            .whereGreaterThanOrEqualTo("registrationDate", startDate)
            .whereLessThanOrEqualTo("registrationDate", endDate)
            .get()
            .await()
            .size()

        // Get new carriers
        val newCarriers = carriersCollection
            .whereGreaterThanOrEqualTo("registrationDate", startDate)
            .whereLessThanOrEqualTo("registrationDate", endDate)
            .get()
            .await()
            .size()

        // Get financial stats
        val transactions = transactionsCollection
            .whereGreaterThanOrEqualTo("createdAt", startDate)
            .whereLessThanOrEqualTo("createdAt", endDate)
            .get()
            .await()

        var totalVolume = 0.0
        var netRevenue = 0.0
        var pendingPayouts = 0.0
        var refundsProcessed = 0.0

        transactions.documents.forEach { doc ->
            val amount = doc.getDouble("amount") ?: 0.0
            val type = doc.getString("type") ?: ""
            val status = doc.getString("status") ?: ""

            when (type) {
                "PAYMENT" -> totalVolume += amount
                "SERVICE_FEE" -> netRevenue += amount
                "PAYOUT" -> {
                    if (status == "PENDING" || status == "PROCESSING") {
                        pendingPayouts += amount
                    }
                }
                "REFUND" -> refundsProcessed += amount
            }
        }

        // Get active users count
        val activeLoaders = loadersCollection
            .whereEqualTo("isActive", true)
            .get()
            .await()
            .size()

        val activeCarriers = carriersCollection
            .whereEqualTo("isActive", true)
            .get()
            .await()
            .size()

        return Analytics(
            id = "${period}_${startDate}_$endDate",
            period = period,
            startDate = startDate,
            endDate = endDate,
            totalShipments = totalShipments,
            completedShipments = completedShipments,
            cancelledShipments = cancelledShipments,
            inTransitShipments = inTransitShipments,
            delayedShipments = delayedShipments,
            newLoaders = newLoaders,
            newCarriers = newCarriers,
            activeUsers = activeLoaders + activeCarriers,
            totalVolume = totalVolume,
            netRevenue = netRevenue,
            pendingPayouts = pendingPayouts,
            refundsProcessed = refundsProcessed,
            lastUpdated = System.currentTimeMillis()
        )
    }

    // Get top performing routes
    suspend fun getTopRoutes(limit: Int = 5): Result<List<TopRoute>> {
        return try {
            val shipments = shipmentsCollection
                .whereEqualTo("status", ShipmentStatus.COMPLETED.name)
                .get()
                .await()

            val routeMap = mutableMapOf<String, Pair<Int, Double>>()

            shipments.documents.forEach { doc ->
                val origin = "${doc.getString("pickupCity")}, ${doc.getString("pickupState")}"
                val destination = "${doc.getString("deliveryCity")}, ${doc.getString("deliveryState")}"
                val routeKey = "$origin -> $destination"
                val price = doc.getDouble("finalPrice") ?: 0.0

                val existing = routeMap[routeKey] ?: Pair(0, 0.0)
                routeMap[routeKey] = Pair(existing.first + 1, existing.second + price)
            }

            val topRoutes = routeMap.entries
                .sortedByDescending { it.value.first }
                .take(limit)
                .map { (route, data) ->
                    val parts = route.split(" -> ")
                    TopRoute(
                        origin = parts.getOrElse(0) { "" },
                        destination = parts.getOrElse(1) { "" },
                        loadCount = data.first,
                        totalRevenue = data.second
                    )
                }

            Result.success(topRoutes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get daily analytics for chart
    suspend fun getDailyAnalytics(startDate: Long, endDate: Long): Result<List<DailyAnalytics>> {
        return try {
            val shipments = shipmentsCollection
                .whereGreaterThanOrEqualTo("createdAt", startDate)
                .whereLessThanOrEqualTo("createdAt", endDate)
                .get()
                .await()

            val transactions = transactionsCollection
                .whereGreaterThanOrEqualTo("createdAt", startDate)
                .whereLessThanOrEqualTo("createdAt", endDate)
                .whereEqualTo("type", "PAYMENT")
                .get()
                .await()

            val dailyMap = mutableMapOf<Long, Triple<Int, Int, Double>>()

            // Count shipments per day
            shipments.documents.forEach { doc ->
                val createdAt = doc.getLong("createdAt") ?: 0
                val dayTimestamp = (createdAt / (24 * 60 * 60 * 1000)) * (24 * 60 * 60 * 1000)
                val existing = dailyMap[dayTimestamp] ?: Triple(0, 0, 0.0)
                dailyMap[dayTimestamp] = Triple(existing.first + 1, existing.second, existing.third)
            }

            // Add revenue per day
            transactions.documents.forEach { doc ->
                val createdAt = doc.getLong("createdAt") ?: 0
                val amount = doc.getDouble("amount") ?: 0.0
                val dayTimestamp = (createdAt / (24 * 60 * 60 * 1000)) * (24 * 60 * 60 * 1000)
                val existing = dailyMap[dayTimestamp] ?: Triple(0, 0, 0.0)
                dailyMap[dayTimestamp] = Triple(existing.first, existing.second, existing.third + amount)
            }

            val dailyAnalytics = dailyMap.map { (date, data) ->
                DailyAnalytics(
                    date = date,
                    loads = data.first,
                    carriers = data.second,
                    revenue = data.third
                )
            }.sortedBy { it.date }

            Result.success(dailyAnalytics)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get real-time dashboard stats
    suspend fun getDashboardStats(): Result<DashboardStats> {
        return try {
            // Active shipments
            val activeShipments = shipmentsCollection
                .whereIn("status", listOf(
                    ShipmentStatus.IN_TRANSIT.name,
                    ShipmentStatus.PICKED_UP.name,
                    ShipmentStatus.ASSIGNED.name
                ))
                .get()
                .await()
                .size()

            // Delayed shipments (simplified - would need proper delay logic)
            val delayedShipments = shipmentsCollection
                .whereEqualTo("status", ShipmentStatus.IN_TRANSIT.name)
                .whereLessThan("estimatedArrival", System.currentTimeMillis())
                .get()
                .await()
                .size()

            // Open disputes
            val openDisputes = db.collection("disputes")
                .whereIn("status", listOf("OPEN", "IN_REVIEW"))
                .get()
                .await()
                .size()

            // Pending payouts
            val pendingPayouts = db.collection("payouts")
                .whereIn("status", listOf("PENDING", "PROCESSING"))
                .get()
                .await()

            var pendingPayoutAmount = 0.0
            var pendingPayoutCount = 0
            pendingPayouts.documents.forEach { doc ->
                pendingPayoutAmount += doc.getDouble("amount") ?: 0.0
                pendingPayoutCount++
            }

            // Pending verifications
            val pendingLoaderVerifications = loadersCollection
                .whereEqualTo("verificationStatus", "PENDING")
                .get()
                .await()
                .size()

            val pendingCarrierVerifications = carriersCollection
                .whereEqualTo("verificationStatus", "PENDING")
                .get()
                .await()
                .size()

            Result.success(DashboardStats(
                activeShipments = activeShipments,
                delayedShipments = delayedShipments,
                openDisputes = openDisputes,
                pendingPayouts = pendingPayoutCount,
                pendingPayoutAmount = pendingPayoutAmount,
                pendingVerifications = pendingLoaderVerifications + pendingCarrierVerifications
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get shipment volume trends
    fun getShipmentVolumeTrends(days: Int = 7): Flow<List<DailyAnalytics>> = flow {
        try {
            val endDate = System.currentTimeMillis()
            val startDate = endDate - (days.toLong() * 24 * 60 * 60 * 1000)

            val result = getDailyAnalytics(startDate, endDate)
            result.onSuccess { emit(it) }
            result.onFailure { emit(emptyList()) }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}

data class DashboardStats(
    val activeShipments: Int = 0,
    val delayedShipments: Int = 0,
    val openDisputes: Int = 0,
    val pendingPayouts: Int = 0,
    val pendingPayoutAmount: Double = 0.0,
    val pendingVerifications: Int = 0
)

