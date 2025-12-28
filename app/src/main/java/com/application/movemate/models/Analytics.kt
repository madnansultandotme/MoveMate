package com.application.movemate.models

data class Analytics(
    val id: String = "",
    val period: String = "", // "daily", "weekly", "monthly", "yearly"
    val startDate: Long = 0,
    val endDate: Long = 0,

    // Shipment Metrics
    val totalShipments: Int = 0,
    val completedShipments: Int = 0,
    val cancelledShipments: Int = 0,
    val inTransitShipments: Int = 0,
    val delayedShipments: Int = 0,

    // User Metrics
    val newLoaders: Int = 0,
    val newCarriers: Int = 0,
    val activeUsers: Int = 0,

    // Financial Metrics
    val totalVolume: Double = 0.0,
    val netRevenue: Double = 0.0,
    val pendingPayouts: Double = 0.0,
    val refundsProcessed: Double = 0.0,

    // Performance Metrics
    val averageDeliveryTime: Double = 0.0, // in hours
    val onTimeDeliveryRate: Double = 0.0, // percentage

    // Growth Metrics
    val shipmentsGrowth: Double = 0.0, // percentage vs previous period
    val revenueGrowth: Double = 0.0,
    val userGrowth: Double = 0.0,

    val lastUpdated: Long = System.currentTimeMillis()
)

data class TopRoute(
    val origin: String = "",
    val destination: String = "",
    val loadCount: Int = 0,
    val totalRevenue: Double = 0.0
)

data class DailyAnalytics(
    val date: Long = 0,
    val loads: Int = 0,
    val carriers: Int = 0,
    val revenue: Double = 0.0
)

