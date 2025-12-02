package com.application.movemate.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.movemate.models.Loader
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class ProfileSaveState {
    object Idle : ProfileSaveState()
    object Saving : ProfileSaveState()
    object Success : ProfileSaveState()
    data class Error(val message: String) : ProfileSaveState()
}

class LoaderProfileViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val _profile = MutableStateFlow<Loader?>(null)
    val profile: StateFlow<Loader?> = _profile

    private val _saveState = MutableStateFlow<ProfileSaveState>(ProfileSaveState.Idle)
    val saveState: StateFlow<ProfileSaveState> = _saveState

    fun loadProfile(loaderId: String) {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("loaders").document(loaderId).get().await()
                _profile.value = snapshot.toObject(Loader::class.java)
            } catch (e: Exception) {
                _saveState.value = ProfileSaveState.Error(e.message ?: "Failed to load profile")
            }
        }
    }

    fun savePersonalInfo(loaderId: String, name: String, phone: String, email: String) {
        viewModelScope.launch {
            _saveState.value = ProfileSaveState.Saving
            try {
                db.collection("loaders").document(loaderId)
                    .update(
                        mapOf(
                            "name" to name,
                            "phone" to phone,
                            "email" to email
                        )
                    )
                    .await()
                _saveState.value = ProfileSaveState.Success
                loadProfile(loaderId)
            } catch (e: Exception) {
                _saveState.value = ProfileSaveState.Error(e.message ?: "Failed to save profile")
            }
        }
    }

    fun savePaymentMethod(loaderId: String, cardNumber: String, bankAccount: String) {
        viewModelScope.launch {
            _saveState.value = ProfileSaveState.Saving
            try {
                db.collection("loaders").document(loaderId)
                    .update(
                        mapOf(
                            "payment.cardNumber" to cardNumber,
                            "payment.bankAccount" to bankAccount
                        )
                    )
                    .await()
                _saveState.value = ProfileSaveState.Success
            } catch (e: Exception) {
                _saveState.value = ProfileSaveState.Error(e.message ?: "Failed to save payment method")
            }
        }
    }

    fun resetSaveState() {
        _saveState.value = ProfileSaveState.Idle
    }
}

