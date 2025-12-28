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

class ActiveShipmentsViewModel : ViewModel() {

    private val shipmentRepository = ShipmentRepository()

    private val _shipments = MutableStateFlow<List<Shipment>>(emptyList())
    val shipments: StateFlow<List<Shipment>> = _shipments.asStateFlow()

    private val _filteredShipments = MutableStateFlow<List<Shipment>>(emptyList())
    val filteredShipments: StateFlow<List<Shipment>> = _filteredShipments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    fun fetchActiveShipments(loaderId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            shipmentRepository.getShipmentsByLoaderId(loaderId).collect { allShipments ->
                // Filter only active shipments
                val activeStatuses = listOf(
                    ShipmentStatus.ASSIGNED,
                    ShipmentStatus.PICKED_UP,
                    ShipmentStatus.IN_TRANSIT,
                    ShipmentStatus.ARRIVING_SOON
                )
                _shipments.value = allShipments.filter { it.status in activeStatuses }
                applyFilter(_selectedFilter.value)
            }
            _isLoading.value = false
        }
    }

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
        applyFilter(filter)
    }

    private fun applyFilter(filter: String) {
        _filteredShipments.value = when (filter) {
            "Picked Up" -> _shipments.value.filter { it.status == ShipmentStatus.PICKED_UP }
            "In Transit" -> _shipments.value.filter { it.status == ShipmentStatus.IN_TRANSIT }
            "Arriving Soon" -> _shipments.value.filter { it.status == ShipmentStatus.ARRIVING_SOON }
            else -> _shipments.value
        }
    }

    fun searchShipments(query: String): List<Shipment> {
        return _shipments.value.filter { shipment ->
            shipment.id.contains(query, ignoreCase = true) ||
            shipment.pickupCity.contains(query, ignoreCase = true) ||
            shipment.deliveryCity.contains(query, ignoreCase = true) ||
            shipment.carrierName.contains(query, ignoreCase = true)
        }
    }

    fun refreshShipments(loaderId: String) {
        fetchActiveShipments(loaderId)
    }

    fun clearError() {
        _error.value = null
    }
}
