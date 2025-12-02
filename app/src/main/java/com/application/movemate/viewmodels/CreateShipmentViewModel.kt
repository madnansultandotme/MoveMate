package com.application.movemate.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.movemate.models.Shipment
import com.application.movemate.models.ShipmentStatus
import com.application.movemate.repositories.ShipmentRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreateShipmentViewModel : ViewModel() {

    private val repository = ShipmentRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _shipmentCreated = MutableStateFlow(false)
    val shipmentCreated: StateFlow<Boolean> = _shipmentCreated

    private val _shipmentId = MutableStateFlow<String?>(null)
    val shipmentId: StateFlow<String?> = _shipmentId

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun createShipment(
        goodsType: String,
        weight: Double,
        description: String,
        vehicleType: String,
        pickupAddress: String,
        pickupLat: Double,
        pickupLng: Double,
        deliveryAddress: String,
        deliveryLat: Double,
        deliveryLng: Double,
        estimatedPrice: Double,
        notes: String
    ) {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                _error.value = "User not authenticated"
                return@launch
            }

            val shipment = Shipment(
                id = "",
                loaderId = currentUser.uid,
                loaderName = currentUser.displayName ?: currentUser.email ?: "Unknown",
                carrierId = "",
                carrierName = "",
                goodsType = goodsType,
                weight = weight,
                description = description,
                vehicleType = vehicleType,
                pickupAddress = pickupAddress,
                pickupLat = pickupLat,
                pickupLng = pickupLng,
                deliveryAddress = deliveryAddress,
                deliveryLat = deliveryLat,
                deliveryLng = deliveryLng,
                estimatedPrice = estimatedPrice,
                status = ShipmentStatus.OPEN_FOR_BIDS,
                notes = notes,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            val result = repository.createShipment(shipment)
            if (result.isSuccess) {
                _shipmentId.value = result.getOrNull()
                _shipmentCreated.value = true
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to create shipment"
            }
        }
    }
}
