package com.application.movemate.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UpdateShipmentStatusViewModel : ViewModel() {

    private val db = Firebase.firestore

    fun updateShipmentStatus(shipmentId: String, status: String) {
        viewModelScope.launch {
            try {
                db.collection("shipments").document(shipmentId)
                    .update("status", status)
                    .await()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

