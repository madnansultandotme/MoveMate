package com.application.movemate.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.movemate.models.Carrier
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CarrierViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val _carrier = MutableStateFlow<Carrier?>(null)
    val carrier: StateFlow<Carrier?> = _carrier

    fun fetchCarrier(carrierId: String) {
        viewModelScope.launch {
            val snapshot = db.collection("carriers").document(carrierId).get().await()
            _carrier.value = snapshot.toObject(Carrier::class.java)
        }
    }
}

