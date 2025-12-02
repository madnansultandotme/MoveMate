package com.application.movemate.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.movemate.models.Transaction
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PaymentViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    fun fetchTransactions(loaderId: String) {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("transactions")
                    .whereEqualTo("loaderId", loaderId)
                    .get()
                    .await()
                _transactions.value = snapshot.toObjects(Transaction::class.java)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

