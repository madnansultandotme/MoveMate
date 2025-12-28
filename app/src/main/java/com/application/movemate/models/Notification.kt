package com.application.movemate.models

data class Notification(
    val id: String = "",
    val userId: String = "",
    val userRole: String = "", // "Loader", "Carrier", "Admin"
    val type: NotificationType = NotificationType.GENERAL,
    val title: String = "",
    val message: String = "",
    val data: Map<String, String> = emptyMap(), // Additional data like shipmentId, disputeId, etc.
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

enum class NotificationType {
    GENERAL,
    SHIPMENT_UPDATE,
    BID_RECEIVED,
    BID_ACCEPTED,
    BID_REJECTED,
    PAYMENT_RECEIVED,
    PAYOUT_PROCESSED,
    DISPUTE_OPENED,
    DISPUTE_RESOLVED,
    VERIFICATION_UPDATE,
    NEW_LOAD_AVAILABLE
}

