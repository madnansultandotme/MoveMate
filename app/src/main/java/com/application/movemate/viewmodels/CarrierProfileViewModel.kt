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

class CarrierProfileViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val _profile = MutableStateFlow<Carrier?>(null)
    val profile: StateFlow<Carrier?> = _profile

    fun loadProfile(carrierId: String) {
        viewModelScope.launch {
            val snapshot = db.collection("carriers").document(carrierId).get().await()
            _profile.value = snapshot.toObject(Carrier::class.java)
        }
    }

    fun savePersonalInfo(carrierId: String, name: String, email: String) {
        viewModelScope.launch {
            db.collection("carriers").document(carrierId)
                .update(mapOf(
                    "name" to name,
                    "email" to email
                ))
                .await()
            loadProfile(carrierId)
        }
    }

    fun saveVehicleInfo(carrierId: String, vehicleType: String, capacity: String) {
        viewModelScope.launch {
            db.collection("carriers").document(carrierId)
                .update(mapOf(
                    "vehicleType" to vehicleType,
                    "capacity" to capacity
                ))
                .await()
            loadProfile(carrierId)
        }
    }

    fun saveBankDetails(carrierId: String, bankName: String, accountNumber: String) {
        viewModelScope.launch {
            db.collection("carriers").document(carrierId)
                .update(mapOf(
                    "bankName" to bankName,
                    "accountNumber" to accountNumber
                ))
                .await()
            loadProfile(carrierId)
        }
    }
}

