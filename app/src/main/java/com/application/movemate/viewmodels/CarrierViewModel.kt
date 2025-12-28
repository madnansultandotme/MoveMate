package com.application.movemate.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.movemate.models.Carrier
import com.application.movemate.models.Shipment
import com.application.movemate.models.CarrierBid
import com.application.movemate.models.VehicleInfo
import com.application.movemate.models.BankInfo
import com.application.movemate.repositories.CarrierRepository
import com.application.movemate.repositories.ShipmentRepository
import com.application.movemate.repositories.BidRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CarrierViewModel : ViewModel() {

    private val carrierRepository = CarrierRepository()
    private val shipmentRepository = ShipmentRepository()
    private val bidRepository = BidRepository()

    private val _carrier = MutableStateFlow<Carrier?>(null)
    val carrier: StateFlow<Carrier?> = _carrier.asStateFlow()

    private val _shipments = MutableStateFlow<List<Shipment>>(emptyList())
    val shipments: StateFlow<List<Shipment>> = _shipments.asStateFlow()

    private val _bids = MutableStateFlow<List<CarrierBid>>(emptyList())
    val bids: StateFlow<List<CarrierBid>> = _bids.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun fetchCarrier(carrierId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            carrierRepository.getCarrier(carrierId)
                .onSuccess { _carrier.value = it }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun fetchCarrierShipments(carrierId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            shipmentRepository.getShipmentsByCarrierId(carrierId).collect { shipments ->
                _shipments.value = shipments
            }
            _isLoading.value = false
        }
    }

    fun fetchCarrierBids(carrierId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            bidRepository.getBidsByCarrier(carrierId).collect { bids ->
                _bids.value = bids
            }
            _isLoading.value = false
        }
    }

    fun updateCarrier(carrierId: String, updates: Map<String, Any>) {
        viewModelScope.launch {
            _isLoading.value = true
            carrierRepository.updateCarrier(carrierId, updates)
                .onSuccess { fetchCarrier(carrierId) }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun updatePrimaryVehicle(carrierId: String, vehicle: VehicleInfo) {
        viewModelScope.launch {
            carrierRepository.updatePrimaryVehicle(carrierId, vehicle)
                .onSuccess { fetchCarrier(carrierId) }
                .onFailure { _error.value = it.message }
        }
    }

    fun updateBankInfo(carrierId: String, bankInfo: BankInfo) {
        viewModelScope.launch {
            carrierRepository.updateBankInfo(carrierId, bankInfo)
                .onSuccess { fetchCarrier(carrierId) }
                .onFailure { _error.value = it.message }
        }
    }

    fun updateNotificationSettings(carrierId: String, pushEnabled: Boolean, emailEnabled: Boolean) {
        viewModelScope.launch {
            carrierRepository.updateNotificationSettings(carrierId, pushEnabled, emailEnabled)
                .onSuccess { fetchCarrier(carrierId) }
                .onFailure { _error.value = it.message }
        }
    }

    fun updateLastActive(carrierId: String) {
        viewModelScope.launch {
            carrierRepository.updateLastActive(carrierId)
        }
    }

    fun clearError() {
        _error.value = null
    }
}
