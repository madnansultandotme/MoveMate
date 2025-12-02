package com.application.movemate.models

data class Transaction(
    val id: String = "",
    val shipmentId: String = "",
    val loader: String = "",
    val carrier: String = "",
    val amount: Double = 0.0,
    val status: String = ""
)

