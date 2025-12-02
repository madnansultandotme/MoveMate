package com.application.movemate.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.movemate.models.CarrierBid
import com.application.movemate.repositories.ShipmentRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CarrierMyBidsViewModel : ViewModel() {

    private val repository = ShipmentRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _bids = MutableStateFlow<List<CarrierBid>>(emptyList())
    val bids: StateFlow<List<CarrierBid>> = _bids

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchMyBids() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    repository.getBidsByCarrierId(currentUser.uid).collect { bidList ->
                        _bids.value = bidList.sortedByDescending { it.createdAt }
                    }
                } else {
                    _error.value = "User not authenticated"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to fetch bids"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

