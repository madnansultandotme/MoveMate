package com.application.movemate.models

data class Payout(
    val id: String = "",
    val carrierId: String = "",
    val carrierName: String = "",
    val amount: Double = 0.0,
    val status: PayoutStatus = PayoutStatus.PENDING,
    val bankName: String = "",
    val accountNumber: String = "",
    val accountTitle: String = "",
    val requestedAt: Long = System.currentTimeMillis(),
    val processedAt: Long? = null,
    val failureReason: String? = null,
    val referenceNumber: String = ""
)

enum class PayoutStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    CANCELLED
}

