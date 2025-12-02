package com.application.movemate.models

data class User(
    val id: String = "",
    val name: String = "",
    val role: String = "",
    val email: String = "",
    val verificationStatus: String = "",
    val registrationDate: String = ""
)

