package com.application.movemate.models

data class Transaction(
    val id: String = "",
    val shipmentId: String = "",
    val userId: String = "",
    val userRole: String = "", // "Loader" or "Carrier"
    val userName: String = "",
    val type: TransactionType = TransactionType.PAYMENT,
    val amount: Double = 0.0,
    val status: TransactionStatus = TransactionStatus.PENDING,
    val description: String = "",
    val paymentMethod: String = "",
    val referenceNumber: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,

    // For carrier payouts
    val bankName: String = "",
    val accountNumber: String = "",
    val accountTitle: String = ""
)

enum class TransactionType {
    PAYMENT,      // Payment from loader for shipment
    PAYOUT,       // Payout to carrier
    REFUND,       // Refund to loader
    TIP,          // Tip from loader to carrier
    SERVICE_FEE   // Platform service fee
}

enum class TransactionStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    CANCELLED
}
