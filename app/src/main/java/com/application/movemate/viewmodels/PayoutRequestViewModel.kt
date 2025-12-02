package com.application.movemate.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class PayoutState {
    object Idle : PayoutState()
    object Loading : PayoutState()
    object Success : PayoutState()
    data class Error(val message: String) : PayoutState()
}

class PayoutRequestViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val _payoutState = MutableStateFlow<PayoutState>(PayoutState.Idle)
    val payoutState: StateFlow<PayoutState> = _payoutState

    fun requestPayout(carrierId: String, amount: Double) {
        viewModelScope.launch {
            _payoutState.value = PayoutState.Loading
            try {
                val payoutRequest = hashMapOf(
                    "carrierId" to carrierId,
                    "amount" to amount,
                    "status" to "Pending",
                    "timestamp" to System.currentTimeMillis()
                )
                db.collection("payouts").add(payoutRequest).await()
                _payoutState.value = PayoutState.Success
            } catch (e: Exception) {
                _payoutState.value = PayoutState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}

