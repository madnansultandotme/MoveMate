package com.application.movemate.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.movemate.models.Shipment
import com.application.movemate.models.ShipmentStatus
import com.application.movemate.repositories.ShipmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class StatusUpdateState {
    object Idle : StatusUpdateState()
    object Loading : StatusUpdateState()
    object Success : StatusUpdateState()
    data class Error(val message: String) : StatusUpdateState()
}

class UpdateShipmentStatusViewModel : ViewModel() {

    private val shipmentRepository = ShipmentRepository()

    private val _shipment = MutableStateFlow<Shipment?>(null)
    val shipment: StateFlow<Shipment?> = _shipment.asStateFlow()

    private val _updateState = MutableStateFlow<StatusUpdateState>(StatusUpdateState.Idle)
    val updateState: StateFlow<StatusUpdateState> = _updateState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun fetchShipment(shipmentId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            shipmentRepository.getShipment(shipmentId)
                .onSuccess { _shipment.value = it }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun updateShipmentStatus(shipmentId: String, status: ShipmentStatus) {
        viewModelScope.launch {
            _updateState.value = StatusUpdateState.Loading
            val updates = mutableMapOf<String, Any>(
                "status" to status.name,
                "updatedAt" to System.currentTimeMillis()
            )

            // Add timestamp for specific statuses
            when (status) {
                ShipmentStatus.PICKED_UP -> updates["pickedUpAt"] = System.currentTimeMillis()
                ShipmentStatus.DELIVERED -> updates["deliveredAt"] = System.currentTimeMillis()
                ShipmentStatus.COMPLETED -> updates["completedAt"] = System.currentTimeMillis()
                else -> {}
            }

            shipmentRepository.updateShipment(shipmentId, updates)
                .onSuccess {
                    _updateState.value = StatusUpdateState.Success
                    fetchShipment(shipmentId)
                }
                .onFailure {
                    _updateState.value = StatusUpdateState.Error(it.message ?: "Failed to update status")
                }
        }
    }

    fun updateLocationTracking(shipmentId: String, lat: Double, lng: Double, progressPercentage: Int) {
        viewModelScope.launch {
            val updates = mapOf(
                "currentLocationLat" to lat,
                "currentLocationLng" to lng,
                "lastLocationUpdate" to System.currentTimeMillis(),
                "progressPercentage" to progressPercentage
            )
            shipmentRepository.updateShipment(shipmentId, updates)
        }
    }

    fun markAsPickedUp(shipmentId: String) {
        updateShipmentStatus(shipmentId, ShipmentStatus.PICKED_UP)
    }

    fun markAsInTransit(shipmentId: String) {
        updateShipmentStatus(shipmentId, ShipmentStatus.IN_TRANSIT)
    }

    fun markAsArrivingSoon(shipmentId: String, estimatedArrival: Long) {
        viewModelScope.launch {
            _updateState.value = StatusUpdateState.Loading
            val updates = mapOf(
                "status" to ShipmentStatus.ARRIVING_SOON.name,
                "estimatedArrival" to estimatedArrival,
                "updatedAt" to System.currentTimeMillis()
            )
            shipmentRepository.updateShipment(shipmentId, updates)
                .onSuccess {
                    _updateState.value = StatusUpdateState.Success
                    fetchShipment(shipmentId)
                }
                .onFailure {
                    _updateState.value = StatusUpdateState.Error(it.message ?: "Failed to update status")
                }
        }
    }

    fun markAsDelivered(
        shipmentId: String,
        proofImages: List<String>,
        signature: String?,
        receiverName: String,
        notes: String
    ) {
        viewModelScope.launch {
            _updateState.value = StatusUpdateState.Loading
            val updates = mutableMapOf<String, Any>(
                "status" to ShipmentStatus.DELIVERED.name,
                "deliveredAt" to System.currentTimeMillis(),
                "deliveryProofImages" to proofImages,
                "receiverName" to receiverName,
                "deliveryNotes" to notes,
                "progressPercentage" to 100,
                "updatedAt" to System.currentTimeMillis()
            )
            signature?.let { updates["deliverySignature"] = it }

            shipmentRepository.updateShipment(shipmentId, updates)
                .onSuccess {
                    _updateState.value = StatusUpdateState.Success
                    fetchShipment(shipmentId)
                }
                .onFailure {
                    _updateState.value = StatusUpdateState.Error(it.message ?: "Failed to mark as delivered")
                }
        }
    }

    fun addDeliveryNote(shipmentId: String, note: String) {
        viewModelScope.launch {
            val currentNotes = _shipment.value?.deliveryNotes ?: ""
            val updatedNotes = if (currentNotes.isNotEmpty()) "$currentNotes\n$note" else note
            shipmentRepository.updateShipment(shipmentId, mapOf("deliveryNotes" to updatedNotes))
        }
    }

    fun resetUpdateState() {
        _updateState.value = StatusUpdateState.Idle
    }

    fun clearError() {
        _error.value = null
    }
}
