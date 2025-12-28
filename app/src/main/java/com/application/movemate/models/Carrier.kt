package com.application.movemate.models

data class Carrier(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val profileImageUrl: String = "",

    // Company Details
    val companyName: String = "",
    val companyRegistrationNumber: String = "",
    val ntnNumber: String = "",
    val taxId: String = "",
    val address: String = "",

    // Verification & KYC
    val verificationStatus: VerificationStatus = VerificationStatus.PENDING,
    val businessDocuments: BusinessDocuments = BusinessDocuments(),
    val registrationDate: Long = System.currentTimeMillis(),
    val approvedAt: Long? = null,
    val rejectionReason: String? = null,

    // Fleet Information
    val vehicles: List<VehicleInfo> = emptyList(),
    val totalVehicles: Int = 0,
    val primaryVehicle: VehicleInfo? = null,

    // Performance
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val totalShipments: Int = 0,
    val completedShipments: Int = 0,
    val onTimeDeliveryRate: Double = 0.0,
    val carrierTier: String = "", // "Gold", "Silver", "Bronze"

    // Financial
    val bankInfo: BankInfo = BankInfo(),
    val totalEarnings: Double = 0.0,
    val availableBalance: Double = 0.0,
    val pendingPayments: Double = 0.0,
    val lifetimeEarnings: Double = 0.0,

    // Bidding Stats
    val activeBids: Int = 0,
    val pendingBids: Int = 0,
    val acceptedBidsThisWeek: Int = 0,

    // Settings
    val pushNotificationsEnabled: Boolean = true,
    val emailNotificationsEnabled: Boolean = true,
    val twoFactorEnabled: Boolean = false,

    // Account Status
    val isActive: Boolean = true,
    val isVerified: Boolean = false,
    val suspendedAt: Long? = null,
    val suspensionReason: String? = null,
    val lastActiveAt: Long? = null
)

data class BusinessDocuments(
    val companyRegistrationDoc: String = "",
    val ntnDoc: String = "",
    val insuranceDoc: String = "",
    val ownerCnicFront: String = "",
    val ownerCnicBack: String = "",
    val uploadedAt: Long = System.currentTimeMillis()
)

data class VehicleInfo(
    val id: String = "",
    val vehicleType: String = "", // "Cargo Van", "Box Truck", "Semi-Truck", etc.
    val vehicleName: String = "", // "Mercedes Sprinter", "Ford Transit"
    val vehicleNumber: String = "",
    val plateNumber: String = "",
    val capacity: Double = 0.0, // in tons or lbs
    val capacityUnit: String = "lbs",
    val registrationImage: String = "",
    val isActive: Boolean = true,
    val isVerified: Boolean = false
)

data class BankInfo(
    val bankName: String = "",
    val accountNumber: String = "",
    val accountTitle: String = "",
    val routingNumber: String = "",
    val isVerified: Boolean = false
)
