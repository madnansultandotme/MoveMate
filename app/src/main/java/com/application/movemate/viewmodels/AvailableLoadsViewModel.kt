package com.application.movemate.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.movemate.models.Shipment
import com.application.movemate.repositories.ShipmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AvailableLoadsViewModel : ViewModel() {

    private val repository = ShipmentRepository()

    private val _loads = MutableStateFlow<List<Shipment>>(emptyList())
    val loads: StateFlow<List<Shipment>> = _loads

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchAvailableLoads()
    }

    fun fetchAvailableLoads() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getAvailableLoads().collect { shipmentList ->
                    _loads.value = shipmentList.sortedByDescending { it.createdAt }
                }
            } catch (e: Exception) {
                // Handle error
                _loads.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}



