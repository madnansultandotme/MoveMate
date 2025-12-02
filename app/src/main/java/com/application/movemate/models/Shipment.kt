package com.application.movemate.models

data class Shipment(
    val id: String = "",
    val loaderId: String = "",  // The shipper who created the shipment request
    val loaderName: String = "",
    val carrierId: String = "",  // The driver/transport company assigned to deliver
    val carrierName: String = "",

    // Shipment Details
    val goodsType: String = "",
    val weight: Double = 0.0,
    val description: String = "",
    val vehicleType: String = "",

    // Locations
    val pickupAddress: String = "",
    val pickupLat: Double = 0.0,
    val pickupLng: Double = 0.0,
    val deliveryAddress: String = "",
    val deliveryLat: Double = 0.0,
    val deliveryLng: Double = 0.0,

    // Pricing
    val estimatedPrice: Double = 0.0,
    val finalPrice: Double? = null,

    // Status & Tracking
    val status: ShipmentStatus = ShipmentStatus.DRAFT,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val pickupTime: Long? = null,
    val deliveryTime: Long? = null,

    // Documents
    val shipmentImages: List<String> = emptyList(),
    val deliveryProof: String? = null,
    val deliverySignature: String? = null,

    // Additional Info
    val notes: String = "",
    val invoiceId: String? = null
)

enum class ShipmentStatus {
    DRAFT,
    OPEN_FOR_BIDS,
    ASSIGNED,
    PICKED_UP,
    IN_TRANSIT,
    DELIVERED,
    COMPLETED,
    CANCELLED,
    DISPUTED
}

data class CarrierBid(
    val id: String = "",
    val shipmentId: String = "",
    val carrierId: String = "",  // The carrier (driver) placing the bid
    val carrierName: String = "",
    val carrierRating: Double = 0.0,
    val bidAmount: Double = 0.0,
    val estimatedDeliveryTime: Long = 0,
    val message: String = "",
    val status: BidStatus = BidStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)

enum class BidStatus {
    PENDING,
    ACCEPTED,
    REJECTED
}

