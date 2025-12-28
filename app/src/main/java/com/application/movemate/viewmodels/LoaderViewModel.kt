package com.application.movemate.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.movemate.models.Loader
import com.application.movemate.models.Shipment
import com.application.movemate.repositories.LoaderRepository
import com.application.movemate.repositories.ShipmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoaderViewModel : ViewModel() {

    private val loaderRepository = LoaderRepository()
    private val shipmentRepository = ShipmentRepository()

    private val _loader = MutableStateFlow<Loader?>(null)
    val loader: StateFlow<Loader?> = _loader.asStateFlow()

    private val _shipments = MutableStateFlow<List<Shipment>>(emptyList())
    val shipments: StateFlow<List<Shipment>> = _shipments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun fetchLoader(loaderId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            loaderRepository.getLoader(loaderId)
                .onSuccess { _loader.value = it }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun fetchLoaderShipments(loaderId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            shipmentRepository.getShipmentsByLoaderId(loaderId).collect { shipments ->
                _shipments.value = shipments
            }
            _isLoading.value = false
        }
    }

    fun updateLoader(loaderId: String, updates: Map<String, Any>) {
        viewModelScope.launch {
            _isLoading.value = true
            loaderRepository.updateLoader(loaderId, updates)
                .onSuccess { fetchLoader(loaderId) }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun updateNotificationSettings(loaderId: String, pushEnabled: Boolean, emailEnabled: Boolean) {
        viewModelScope.launch {
            loaderRepository.updateNotificationSettings(loaderId, pushEnabled, emailEnabled)
                .onSuccess { fetchLoader(loaderId) }
                .onFailure { _error.value = it.message }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
