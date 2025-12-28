package com.application.movemate.models

data class Admin(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: AdminRole = AdminRole.ADMIN,
    val profileImageUrl: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long? = null,
    val permissions: List<String> = emptyList()
)

enum class AdminRole {
    SUPER_ADMIN,
    ADMIN,
    MODERATOR,
    SUPPORT
}

