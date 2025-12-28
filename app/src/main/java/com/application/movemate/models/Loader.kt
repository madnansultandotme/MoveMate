package com.application.movemate.models

data class Loader(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val profileImageUrl: String = "",

    // Company Details
    val companyName: String = "",
    val taxId: String = "",
    val businessAddress: String = "",

    // Verification & KYC
    val verificationStatus: VerificationStatus = VerificationStatus.PENDING,
    val businessDocuments: LoaderDocuments = LoaderDocuments(),
    val registrationDate: Long = System.currentTimeMillis(),
    val approvedAt: Long? = null,
    val rejectionReason: String? = null,

    // Shipping Preferences
    val defaultPickupAddress: String = "",
    val preferredVehicleType: String = "",
    val defaultCargoCategory: String = "",
    val typicallyShips: List<String> = emptyList(), // "Full Truckload", "Part Load", "Perishables"

    // Statistics
    val totalShipments: Int = 0,
    val activeShipments: Int = 0,
    val completedShipments: Int = 0,
    val pendingBids: Int = 0,
    val monthlySpend: Double = 0.0,

    // Payment Info
    val paymentMethod: PaymentMethod? = null,

    // Settings
    val pushNotificationsEnabled: Boolean = true,
    val emailNotificationsEnabled: Boolean = true,

    // Account Status
    val isActive: Boolean = true,
    val suspendedAt: Long? = null,
    val suspensionReason: String? = null
)

data class LoaderDocuments(
    val taxIdDocument: String = "",
    val businessRegistrationDoc: String = "",
    val uploadedAt: Long = System.currentTimeMillis()
)

data class PaymentMethod(
    val id: String = "",
    val type: String = "", // "Visa", "Mastercard", "Bank"
    val lastFourDigits: String = "",
    val expiryDate: String = "",
    val isDefault: Boolean = false
)
