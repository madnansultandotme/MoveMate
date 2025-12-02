package com.application.movemate.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.movemate.models.Shipment
import com.application.movemate.repositories.ShipmentRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoaderMyShipmentsViewModel : ViewModel() {

    private val repository = ShipmentRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _shipments = MutableStateFlow<List<Shipment>>(emptyList())
    val shipments: StateFlow<List<Shipment>> = _shipments

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchMyShipments() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    repository.getShipmentsByLoaderId(currentUser.uid).collect { shipmentList ->
                        _shipments.value = shipmentList.sortedByDescending { it.createdAt }
                    }
                } else {
                    _error.value = "User not authenticated"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to fetch shipments"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

