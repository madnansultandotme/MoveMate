package com.application.movemate.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.movemate.models.Payout
import com.application.movemate.models.PayoutStatus
import com.application.movemate.models.Carrier
import com.application.movemate.repositories.PayoutRepository
import com.application.movemate.repositories.CarrierRepository
import com.application.movemate.repositories.CarrierPayoutStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PayoutState {
    object Idle : PayoutState()
    object Loading : PayoutState()
    object Success : PayoutState()
    data class Error(val message: String) : PayoutState()
}

class PayoutRequestViewModel : ViewModel() {

    private val payoutRepository = PayoutRepository()
    private val carrierRepository = CarrierRepository()

    private val _payoutState = MutableStateFlow<PayoutState>(PayoutState.Idle)
    val payoutState: StateFlow<PayoutState> = _payoutState.asStateFlow()

    private val _carrier = MutableStateFlow<Carrier?>(null)
    val carrier: StateFlow<Carrier?> = _carrier.asStateFlow()

    private val _recentPayouts = MutableStateFlow<List<Payout>>(emptyList())
    val recentPayouts: StateFlow<List<Payout>> = _recentPayouts.asStateFlow()

    private val _payoutStats = MutableStateFlow<CarrierPayoutStats?>(null)
    val payoutStats: StateFlow<CarrierPayoutStats?> = _payoutStats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun fetchCarrierData(carrierId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            carrierRepository.getCarrier(carrierId)
                .onSuccess { _carrier.value = it }
            _isLoading.value = false
        }
    }

    fun fetchRecentPayouts(carrierId: String) {
        viewModelScope.launch {
            payoutRepository.getRecentPayoutsForCarrier(carrierId, 5).collect { payouts ->
                _recentPayouts.value = payouts
            }
        }
    }

    fun fetchPayoutStats(carrierId: String) {
        viewModelScope.launch {
            payoutRepository.getCarrierPayoutStats(carrierId)
                .onSuccess { _payoutStats.value = it }
        }
    }

    fun requestPayout(carrierId: String, carrierName: String, amount: Double, bankName: String, accountNumber: String, accountTitle: String) {
        viewModelScope.launch {
            _payoutState.value = PayoutState.Loading

            // Check if carrier has sufficient balance
            val carrier = _carrier.value
            if (carrier == null || carrier.availableBalance < amount) {
                _payoutState.value = PayoutState.Error("Insufficient balance")
                return@launch
            }

            val payout = Payout(
                carrierId = carrierId,
                carrierName = carrierName,
                amount = amount,
                status = PayoutStatus.PENDING,
                bankName = bankName,
                accountNumber = accountNumber,
                accountTitle = accountTitle,
                requestedAt = System.currentTimeMillis()
            )

            payoutRepository.requestPayout(payout)
                .onSuccess {
                    // Deduct from carrier's available balance
                    carrierRepository.updateCarrier(carrierId, mapOf(
                        "availableBalance" to (carrier.availableBalance - amount),
                        "pendingPayments" to (carrier.pendingPayments + amount)
                    ))
                    _payoutState.value = PayoutState.Success
                    fetchCarrierData(carrierId)
                    fetchRecentPayouts(carrierId)
                }
                .onFailure {
                    _payoutState.value = PayoutState.Error(it.message ?: "An unknown error occurred")
                }
        }
    }

    fun resetState() {
        _payoutState.value = PayoutState.Idle
    }
}
