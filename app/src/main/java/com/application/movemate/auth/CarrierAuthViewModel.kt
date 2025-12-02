package com.application.movemate.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.movemate.models.Carrier
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CarrierAuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _user = MutableStateFlow<FirebaseUser?>(null)
    val user: StateFlow<FirebaseUser?> = _user

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = repository.login(email, password)
            if (result.isSuccess) {
                _user.value = result.getOrNull()
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            val result = repository.registerUser(email, password)
            if (result.isSuccess) {
                val firebaseUser = result.getOrNull()
                _user.value = firebaseUser
                // Save carrier details to Firestore
                firebaseUser?.let {
                    val carrier = Carrier(id = it.uid, name = name, email = email)
                    repository.addUserDetails(it.uid, carrier, "carriers")
                }
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun logout() {
        repository.logout()
        _user.value = null
    }
}

