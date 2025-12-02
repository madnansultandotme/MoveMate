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

class LoadDetailsViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val _shipment = MutableStateFlow<Shipment?>(null)
    val shipment: StateFlow<Shipment?> = _shipment

    private val _isAccepted = MutableStateFlow(false)
    val isAccepted: StateFlow<Boolean> = _isAccepted

    fun fetchShipmentDetails(shipmentId: String) {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("shipments").document(shipmentId).get().await()
                _shipment.value = snapshot.toObject(Shipment::class.java)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun acceptLoad(carrierId: String) {
        viewModelScope.launch {
            val shipmentId = _shipment.value?.id
            if (shipmentId != null) {
                try {
                    db.collection("shipments").document(shipmentId)
                        .update(
                            mapOf(
                                "status" to "Assigned",
                                "carrierId" to carrierId
                            )
                        )
                        .await()
                    _isAccepted.value = true
                } catch (e: Exception) {
                    // Handle error
                }
            }
        }
    }
}

