package com.application.movemate.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.movemate.models.Shipment
import com.application.movemate.models.CarrierBid
import com.application.movemate.models.BidStatus
import com.application.movemate.repositories.ShipmentRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CarrierLoadDetailsViewModel : ViewModel() {

    private val repository = ShipmentRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _shipment = MutableStateFlow<Shipment?>(null)
    val shipment: StateFlow<Shipment?> = _shipment

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _bidSubmitted = MutableStateFlow(false)
    val bidSubmitted: StateFlow<Boolean> = _bidSubmitted

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchShipment(shipmentId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getShipment(shipmentId)
                if (result.isSuccess) {
                    _shipment.value = result.getOrNull()
                } else {
                    _error.value = "Failed to load shipment"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load shipment"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun submitBid(
        shipmentId: String,
        bidAmount: Double,
        estimatedDays: Int,
        message: String
    ) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser
                if (currentUser == null) {
                    _error.value = "User not authenticated"
                    return@launch
                }

                val bid = CarrierBid(
                    id = "",
                    shipmentId = shipmentId,
                    carrierId = currentUser.uid,
                    carrierName = currentUser.displayName ?: currentUser.email ?: "Unknown",
                    carrierRating = 4.5, // TODO: Get actual rating from user profile
                    bidAmount = bidAmount,
                    estimatedDeliveryTime = System.currentTimeMillis() + (estimatedDays * 24 * 60 * 60 * 1000L),
                    message = message,
                    status = BidStatus.PENDING,
                    createdAt = System.currentTimeMillis()
                )

                val result = repository.submitBid(bid)
                if (result.isSuccess) {
                    _bidSubmitted.value = true
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Failed to submit bid"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to submit bid"
            }
        }
    }
}

