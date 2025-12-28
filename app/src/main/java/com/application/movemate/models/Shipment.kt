package com.application.movemate.models

data class Shipment(
    val id: String = "",
    val loaderId: String = "",
    val loaderName: String = "",
    val loaderProfileImage: String = "",
    val loaderRating: Double = 0.0,
    val loaderTotalLoads: Int = 0,
    val carrierId: String = "",
    val carrierName: String = "",
    val carrierProfileImage: String = "",
    val carrierRating: Double = 0.0,
    val carrierReviewCount: Int = 0,
    val isCarrierVerified: Boolean = false,

    // Cargo Details
    val cargoType: String = "", // "Pallet", "Box", "Vehicle", "Furniture", "Machinery"
    val goodsType: String = "",
    val weight: Double = 0.0,
    val weightUnit: String = "lbs",
    val dimensions: Dimensions = Dimensions(),
    val description: String = "",
    val specialInstructions: String = "",
    val cargoImages: List<String> = emptyList(),

    // Vehicle Requirements
    val vehicleType: String = "", // "Dry Van", "Flatbed", "Reefer", etc.
    val vehicleLength: String = "", // "53'", "48'", etc.

    // Locations
    val pickupAddress: String = "",
    val pickupCity: String = "",
    val pickupState: String = "",
    val pickupLat: Double = 0.0,
    val pickupLng: Double = 0.0,
    val pickupContactName: String = "",
    val pickupContactPhone: String = "",
    val deliveryAddress: String = "",
    val deliveryCity: String = "",
    val deliveryState: String = "",
    val deliveryLat: Double = 0.0,
    val deliveryLng: Double = 0.0,
    val deliveryContactName: String = "",
    val deliveryContactPhone: String = "",

    // Distance & Route
    val distance: Double = 0.0,
    val distanceUnit: String = "mi",
    val estimatedDuration: String = "", // "14h 20m"

    // Scheduling
    val pickupDateStart: Long? = null,
    val pickupDateEnd: Long? = null,
    val deliveryDeadline: Long? = null,
    val estimatedArrival: Long? = null,
    val isFlexibleSchedule: Boolean = false,

    // Pricing
    val targetPrice: Double = 0.0,
    val minPrice: Double = 0.0,
    val maxPrice: Double = 0.0,
    val marketAvgMin: Double = 0.0,
    val marketAvgMax: Double = 0.0,
    val finalPrice: Double? = null,
    val isFixedPrice: Boolean = false,

    // Status & Tracking
    val status: ShipmentStatus = ShipmentStatus.DRAFT,
    val progressPercentage: Int = 0,
    val currentLocationLat: Double? = null,
    val currentLocationLng: Double? = null,
    val lastLocationUpdate: Long? = null,

    // Timestamps
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val postedAt: Long? = null,
    val assignedAt: Long? = null,
    val pickedUpAt: Long? = null,
    val deliveredAt: Long? = null,
    val completedAt: Long? = null,

    // Proof of Delivery
    val deliveryProofImages: List<String> = emptyList(),
    val deliverySignature: String? = null,
    val deliveryNotes: String = "",
    val receiverName: String = "",

    // Bidding
    val bidCount: Int = 0,
    val isUrgent: Boolean = false,

    // Additional
    val notes: String = "",
    val invoiceId: String? = null,
    val disputeId: String? = null
)

data class Dimensions(
    val length: Double = 0.0,
    val width: Double = 0.0,
    val height: Double = 0.0,
    val unit: String = "in"
)

enum class ShipmentStatus {
    DRAFT,
    OPEN_FOR_BIDS,
    ASSIGNED,
    PICKED_UP,
    IN_TRANSIT,
    ARRIVING_SOON,
    DELIVERED,
    COMPLETED,
    CANCELLED,
    DISPUTED
}

data class CarrierBid(
    val id: String = "",
    val shipmentId: String = "",
    val carrierId: String = "",
    val carrierName: String = "",
    val carrierCompanyName: String = "",
    val carrierProfileImage: String = "",
    val carrierRating: Double = 0.0,
    val carrierReviewCount: Int = 0,
    val isCarrierVerified: Boolean = false,
    val bidAmount: Double = 0.0,
    val estimatedDeliveryTime: Long = 0,
    val vehicleType: String = "",
    val message: String = "",
    val status: BidStatus = BidStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis(),
    val isLowestPrice: Boolean = false,
    val isTopRated: Boolean = false
)

enum class BidStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    OUTBID,
    EXPIRED
}
