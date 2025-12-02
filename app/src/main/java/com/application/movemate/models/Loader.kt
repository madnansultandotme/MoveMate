package com.application.movemate.models

data class Loader(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val cnic: String = "",

    // Verification & KYC
    val verificationStatus: VerificationStatus = VerificationStatus.PENDING,
    val kycDocuments: KYCDocuments = KYCDocuments(),
    val registrationDate: Long = System.currentTimeMillis(),
    val approvedAt: Long? = null,
    val rejectionReason: String? = null,

    // Vehicle Information
    val vehicleType: String = "",
    val vehicleCapacity: Double = 0.0,
    val vehicleNumber: String = "",
    val licenseNumber: String = "",

    // Performance
    val rating: Double = 0.0,
    val totalDeliveries: Int = 0,
    val completedDeliveries: Int = 0,

    // Financial
    val payment: PaymentDetails? = null,
    val totalEarnings: Double = 0.0,
    val pendingEarnings: Double = 0.0,

    // Account Status
    val isActive: Boolean = true,
    val suspendedAt: Long? = null,
    val suspensionReason: String? = null
)

data class KYCDocuments(
    val cnicFrontImage: String = "",
    val cnicBackImage: String = "",
    val licenseImage: String = "",
    val vehicleRegistrationImage: String = "",
    val vehicleImages: List<String> = emptyList(),
    val uploadedAt: Long = System.currentTimeMillis()
)

data class PaymentDetails(
    val bankName: String = "",
    val accountNumber: String = "",
    val accountTitle: String = "",
    val cardNumber: String = ""
)


