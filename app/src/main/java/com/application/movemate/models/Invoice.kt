package com.application.movemate.models

data class Invoice(
    val id: String = "",
    val shipmentId: String = "",
    val carrierId: String = "",
    val carrierName: String = "",
    val loaderId: String = "",
    val loaderName: String = "",

    // Amounts
    val baseAmount: Double = 0.0,
    val tax: Double = 0.0,
    val platformFee: Double = 0.0,
    val totalAmount: Double = 0.0,

    // Payment Info
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val paymentMethod: String = "",
    val paidAt: Long? = null,

    // Invoice Details
    val invoiceNumber: String = "",
    val issueDate: Long = System.currentTimeMillis(),
    val dueDate: Long = 0,
    val notes: String = ""
)

enum class PaymentStatus {
    PENDING,
    PAID,
    FAILED,
    REFUNDED
}

