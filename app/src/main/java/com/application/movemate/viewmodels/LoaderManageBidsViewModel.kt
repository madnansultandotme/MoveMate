package com.application.movemate.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.movemate.models.Shipment
import com.application.movemate.models.CarrierBid
import com.application.movemate.repositories.ShipmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoaderManageBidsViewModel : ViewModel() {

    private val repository = ShipmentRepository()

    private val _shipments = MutableStateFlow<List<Shipment>>(emptyList())
    val shipments: StateFlow<List<Shipment>> = _shipments

    private val _bids = MutableStateFlow<List<CarrierBid>>(emptyList())
    val bids: StateFlow<List<CarrierBid>> = _bids

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _bidAccepted = MutableStateFlow(false)
    val bidAccepted: StateFlow<Boolean> = _bidAccepted

    fun fetchShipmentsWithBids(loaderId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getShipmentsByLoaderId(loaderId).collect { shipmentList ->
                    // Only show shipments that are OPEN_FOR_BIDS
                    _shipments.value = shipmentList.filter {
                        it.status.name == "OPEN_FOR_BIDS"
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to fetch shipments"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchBidsForShipment(shipmentId: String) {
        viewModelScope.launch {
            try {
                repository.getBidsForShipment(shipmentId).collect { bidList ->
                    _bids.value = bidList.sortedBy { it.bidAmount }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to fetch bids"
            }
        }
    }

    fun acceptBid(bidId: String, shipmentId: String, carrierId: String, carrierName: String) {
        viewModelScope.launch {
            try {
                val result = repository.acceptBid(bidId, shipmentId, carrierId, carrierName)
                if (result.isSuccess) {
                    _bidAccepted.value = true
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Failed to accept bid"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to accept bid"
            }
        }
    }

    fun resetBidAccepted() {
        _bidAccepted.value = false
    }
}

