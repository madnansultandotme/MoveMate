package com.application.movemate.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.movemate.models.Dispute
import com.application.movemate.models.DisputeStatus
import com.application.movemate.models.DisputePriority
import com.application.movemate.models.DisputeCategory
import com.application.movemate.models.Loader
import com.application.movemate.models.Carrier
import com.application.movemate.repositories.DisputeRepository
import com.application.movemate.repositories.LoaderRepository
import com.application.movemate.repositories.CarrierRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DisputeOperationState {
    object Idle : DisputeOperationState()
    object Loading : DisputeOperationState()
    object Success : DisputeOperationState()
    data class Error(val message: String) : DisputeOperationState()
}

class DisputeViewModel : ViewModel() {

    private val disputeRepository = DisputeRepository()
    private val loaderRepository = LoaderRepository()
    private val carrierRepository = CarrierRepository()

    private val _disputes = MutableStateFlow<List<Dispute>>(emptyList())
    val disputes: StateFlow<List<Dispute>> = _disputes.asStateFlow()

    private val _selectedDispute = MutableStateFlow<Dispute?>(null)
    val selectedDispute: StateFlow<Dispute?> = _selectedDispute.asStateFlow()

    private val _loaderDetails = MutableStateFlow<Loader?>(null)
    val loaderDetails: StateFlow<Loader?> = _loaderDetails.asStateFlow()

    private val _carrierDetails = MutableStateFlow<Carrier?>(null)
    val carrierDetails: StateFlow<Carrier?> = _carrierDetails.asStateFlow()

    private val _operationState = MutableStateFlow<DisputeOperationState>(DisputeOperationState.Idle)
    val operationState: StateFlow<DisputeOperationState> = _operationState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchDisputes()
    }

    fun fetchDisputes() {
        viewModelScope.launch {
            _isLoading.value = true
            disputeRepository.getAllDisputes().collect { disputes ->
                _disputes.value = disputes
            }
            _isLoading.value = false
        }
    }

    fun fetchOpenDisputes() {
        viewModelScope.launch {
            _isLoading.value = true
            disputeRepository.getOpenDisputes().collect { disputes ->
                _disputes.value = disputes
            }
            _isLoading.value = false
        }
    }

    fun fetchDisputesByStatus(status: DisputeStatus) {
        viewModelScope.launch {
            _isLoading.value = true
            disputeRepository.getDisputesByStatus(status).collect { disputes ->
                _disputes.value = disputes
            }
            _isLoading.value = false
        }
    }

    fun fetchHighPriorityDisputes() {
        viewModelScope.launch {
            _isLoading.value = true
            disputeRepository.getHighPriorityDisputes().collect { disputes ->
                _disputes.value = disputes
            }
            _isLoading.value = false
        }
    }

    fun fetchDisputeDetails(disputeId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            disputeRepository.getDispute(disputeId)
                .onSuccess { dispute ->
                    _selectedDispute.value = dispute
                    // Fetch involved parties
                    dispute?.let { d ->
                        if (d.raisedByRole == "Loader") {
                            loaderRepository.getLoader(d.raisedBy).onSuccess { _loaderDetails.value = it }
                            carrierRepository.getCarrier(d.againstUserId).onSuccess { _carrierDetails.value = it }
                        } else {
                            carrierRepository.getCarrier(d.raisedBy).onSuccess { _carrierDetails.value = it }
                            loaderRepository.getLoader(d.againstUserId).onSuccess { _loaderDetails.value = it }
                        }
                    }
                }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun createDispute(dispute: Dispute) {
        viewModelScope.launch {
            _operationState.value = DisputeOperationState.Loading
            disputeRepository.createDispute(dispute)
                .onSuccess {
                    _operationState.value = DisputeOperationState.Success
                    fetchDisputes()
                }
                .onFailure {
                    _operationState.value = DisputeOperationState.Error(it.message ?: "Failed to create dispute")
                }
        }
    }

    fun updateDisputeStatus(disputeId: String, status: DisputeStatus, resolution: String? = null) {
        viewModelScope.launch {
            _operationState.value = DisputeOperationState.Loading
            val updates = mutableMapOf<String, Any>(
                "status" to status.name
            )
            if (status == DisputeStatus.RESOLVED && resolution != null) {
                updates["resolution"] = resolution
                updates["resolvedAt"] = System.currentTimeMillis()
            }

            disputeRepository.updateDispute(disputeId, updates)
                .onSuccess {
                    _operationState.value = DisputeOperationState.Success
                    fetchDisputes()
                    fetchDisputeDetails(disputeId)
                }
                .onFailure {
                    _operationState.value = DisputeOperationState.Error(it.message ?: "Failed to update dispute")
                }
        }
    }

    fun updateDisputePriority(disputeId: String, priority: DisputePriority) {
        viewModelScope.launch {
            _operationState.value = DisputeOperationState.Loading
            disputeRepository.updateDispute(disputeId, mapOf("priority" to priority.name))
                .onSuccess {
                    _operationState.value = DisputeOperationState.Success
                    fetchDisputes()
                }
                .onFailure {
                    _operationState.value = DisputeOperationState.Error(it.message ?: "Failed to update priority")
                }
        }
    }

    fun assignAdmin(disputeId: String, adminId: String, adminName: String) {
        viewModelScope.launch {
            _operationState.value = DisputeOperationState.Loading
            disputeRepository.updateDispute(
                disputeId,
                mapOf(
                    "assignedAdminId" to adminId,
                    "assignedAdminName" to adminName,
                    "status" to DisputeStatus.IN_REVIEW.name
                )
            )
                .onSuccess {
                    _operationState.value = DisputeOperationState.Success
                    fetchDisputes()
                }
                .onFailure {
                    _operationState.value = DisputeOperationState.Error(it.message ?: "Failed to assign admin")
                }
        }
    }

    fun resolveDispute(disputeId: String, resolution: String, notes: String?) {
        viewModelScope.launch {
            _operationState.value = DisputeOperationState.Loading
            val updates = mutableMapOf<String, Any>(
                "status" to DisputeStatus.RESOLVED.name,
                "resolution" to resolution,
                "resolvedAt" to System.currentTimeMillis()
            )
            notes?.let { updates["resolutionNotes"] = it }

            disputeRepository.updateDispute(disputeId, updates)
                .onSuccess {
                    _operationState.value = DisputeOperationState.Success
                    fetchDisputes()
                }
                .onFailure {
                    _operationState.value = DisputeOperationState.Error(it.message ?: "Failed to resolve dispute")
                }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun resetOperationState() {
        _operationState.value = DisputeOperationState.Idle
    }
}
