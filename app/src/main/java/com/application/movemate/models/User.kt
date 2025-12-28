package com.application.movemate.models

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: UserRole = UserRole.LOADER,
    val profileImageUrl: String = "",
    val verificationStatus: VerificationStatus = VerificationStatus.PENDING,
    val registrationDate: Long = System.currentTimeMillis(),
    val lastActiveAt: Long? = null,
    val isActive: Boolean = true,
    val suspendedAt: Long? = null,
    val suspensionReason: String? = null,

    // For notifications
    val pushNotificationsEnabled: Boolean = true,
    val emailNotificationsEnabled: Boolean = true,
    val fcmToken: String = ""
)

enum class UserRole {
    LOADER,
    CARRIER,
    ADMIN
}
