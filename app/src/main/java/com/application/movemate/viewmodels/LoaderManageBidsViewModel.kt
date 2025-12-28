package com.application.movemate.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.movemate.models.Shipment
import com.application.movemate.models.CarrierBid
import com.application.movemate.models.ShipmentStatus
import com.application.movemate.models.BidStatus
import com.application.movemate.repositories.ShipmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class BidOperationState {
    object Idle : BidOperationState()
    object Loading : BidOperationState()
    object Success : BidOperationState()
    data class Error(val message: String) : BidOperationState()
}

class LoaderManageBidsViewModel : ViewModel() {

    private val repository = ShipmentRepository()

    private val _shipments = MutableStateFlow<List<Shipment>>(emptyList())
    val shipments: StateFlow<List<Shipment>> = _shipments.asStateFlow()

    private val _selectedShipment = MutableStateFlow<Shipment?>(null)
    val selectedShipment: StateFlow<Shipment?> = _selectedShipment.asStateFlow()

    private val _bids = MutableStateFlow<List<CarrierBid>>(emptyList())
    val bids: StateFlow<List<CarrierBid>> = _bids.asStateFlow()

    private val _sortedBids = MutableStateFlow<List<CarrierBid>>(emptyList())
    val sortedBids: StateFlow<List<CarrierBid>> = _sortedBids.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _operationState = MutableStateFlow<BidOperationState>(BidOperationState.Idle)
    val operationState: StateFlow<BidOperationState> = _operationState.asStateFlow()

    private val _sortOption = MutableStateFlow("price")
    val sortOption: StateFlow<String> = _sortOption.asStateFlow()

    fun fetchShipmentsWithBids(loaderId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getShipmentsByLoaderId(loaderId).collect { shipmentList ->
                    // Only show shipments that are OPEN_FOR_BIDS
                    _shipments.value = shipmentList.filter {
                        it.status == ShipmentStatus.OPEN_FOR_BIDS
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to fetch shipments"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchShipmentDetails(shipmentId: String) {
        viewModelScope.launch {
            repository.getShipment(shipmentId)
                .onSuccess { _selectedShipment.value = it }
                .onFailure { _error.value = it.message }
        }
    }

    fun fetchBidsForShipment(shipmentId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getBidsForShipment(shipmentId).collect { bidList ->
                    _bids.value = bidList.filter { it.status == BidStatus.PENDING }
                    applySorting(_sortOption.value)
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to fetch bids"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setSortOption(option: String) {
        _sortOption.value = option
        applySorting(option)
    }

    private fun applySorting(option: String) {
        _sortedBids.value = when (option) {
            "price" -> _bids.value.sortedBy { it.bidAmount }
            "rating" -> _bids.value.sortedByDescending { it.carrierRating }
            "date" -> _bids.value.sortedBy { it.estimatedDeliveryTime }
            else -> _bids.value
        }.map { bid ->
            // Mark lowest price and top rated
            bid.copy(
                isLowestPrice = bid.bidAmount == _bids.value.minOfOrNull { it.bidAmount },
                isTopRated = bid.carrierRating == _bids.value.maxOfOrNull { it.carrierRating }
            )
        }
    }

    fun acceptBid(bidId: String, shipmentId: String, carrierId: String, carrierName: String) {
        viewModelScope.launch {
            _operationState.value = BidOperationState.Loading
            try {
                val result = repository.acceptBid(bidId, shipmentId, carrierId, carrierName)
                if (result.isSuccess) {
                    _operationState.value = BidOperationState.Success
                    // Refresh data
                    fetchBidsForShipment(shipmentId)
                } else {
                    _operationState.value = BidOperationState.Error(
                        result.exceptionOrNull()?.message ?: "Failed to accept bid"
                    )
                }
            } catch (e: Exception) {
                _operationState.value = BidOperationState.Error(
                    e.message ?: "Failed to accept bid"
                )
            }
        }
    }

    fun getLowestBid(): CarrierBid? {
        return _bids.value.minByOrNull { it.bidAmount }
    }

    fun getHighestRatedBid(): CarrierBid? {
        return _bids.value.maxByOrNull { it.carrierRating }
    }

    fun resetOperationState() {
        _operationState.value = BidOperationState.Idle
    }

    fun clearError() {
        _error.value = null
    }
}
