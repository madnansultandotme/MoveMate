package com.application.movemate.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.movemate.models.Shipment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ActiveShipmentsViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val _shipments = MutableStateFlow<List<Shipment>>(emptyList())
    val shipments: StateFlow<List<Shipment>> = _shipments

    fun fetchActiveShipments(loaderId: String) {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("shipments")
                    .whereEqualTo("loaderId", loaderId)
                    .whereIn("status", listOf("Assigned", "In Transit", "Out for Delivery"))
                    .get()
                    .await()
                _shipments.value = snapshot.toObjects(Shipment::class.java)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

