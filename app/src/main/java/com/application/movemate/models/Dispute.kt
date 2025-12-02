package com.application.movemate.models

data class Dispute(
    val id: String = "",
    val shipmentId: String = "",

    // Parties Involved
    val raisedBy: String = "", // User ID
    val raisedByRole: String = "", // "Carrier" or "Loader"
    val raisedByName: String = "",
    val againstUserId: String = "",
    val againstUserRole: String = "",
    val againstUserName: String = "",

    // Dispute Details
    val category: DisputeCategory = DisputeCategory.OTHER,
    val subject: String = "",
    val reason: String = "",
    val description: String = "",
    val evidence: List<String> = emptyList(), // Image URLs

    // Status & Resolution
    val status: DisputeStatus = DisputeStatus.OPEN,
    val priority: DisputePriority = DisputePriority.MEDIUM,
    val assignedAdminId: String? = null,
    val assignedAdminName: String? = null,
    val resolution: String? = null,
    val resolutionNotes: String? = null,

    // Timestamps
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val resolvedAt: Long? = null
)

enum class DisputeCategory {
    PAYMENT_ISSUE,
    DAMAGED_GOODS,
    DELIVERY_DELAY,
    UNPROFESSIONAL_BEHAVIOR,
    INCOMPLETE_DELIVERY,
    WRONG_LOCATION,
    OTHER
}

enum class DisputeStatus {
    OPEN,
    IN_REVIEW,
    RESOLVED,
    CLOSED
}

enum class DisputePriority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT
}


