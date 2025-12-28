package com.application.movemate.models

data class Earnings(
    val carrierId: String = "",
    val totalEarnings: Double = 0.0,
    val pendingEarnings: Double = 0.0,
    val availableBalance: Double = 0.0,
    val lifetimeEarnings: Double = 0.0,
    val totalDeliveries: Int = 0,
    val averagePerTrip: Double = 0.0,
    val lastUpdated: Long = System.currentTimeMillis()
)

data class EarningsSummary(
    val period: String = "", // "daily", "weekly", "monthly"
    val startDate: Long = 0,
    val endDate: Long = 0,
    val totalAmount: Double = 0.0,
    val deliveryCount: Int = 0,
    val percentageChange: Double = 0.0, // vs previous period
    val dailyBreakdown: List<DailyEarning> = emptyList()
)

data class DailyEarning(
    val date: Long = 0,
    val amount: Double = 0.0,
    val deliveryCount: Int = 0
)

