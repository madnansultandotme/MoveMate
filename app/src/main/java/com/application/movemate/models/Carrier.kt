package com.application.movemate.models

data class Carrier(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",

    // Company Details
    val companyName: String = "",
    val companyRegistrationNumber: String = "",
    val ntnNumber: String = "",
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

    // Performance
    val rating: Double = 0.0,
    val totalShipments: Int = 0,
    val completedShipments: Int = 0,

    // Financial
    val bankName: String = "",
    val accountNumber: String = "",
    val accountTitle: String = "",
    val totalRevenue: Double = 0.0,
    val pendingPayments: Double = 0.0,

    // Account Status
    val isActive: Boolean = true,
    val suspendedAt: Long? = null,
    val suspensionReason: String? = null
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
    val vehicleType: String = "",
    val vehicleNumber: String = "",
    val capacity: Double = 0.0,
    val registrationImage: String = "",
    val isActive: Boolean = true
)


