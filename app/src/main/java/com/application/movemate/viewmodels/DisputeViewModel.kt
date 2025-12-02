package com.application.movemate.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.movemate.models.Dispute
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DisputeViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val _disputes = MutableStateFlow<List<Dispute>>(emptyList())
    val disputes: StateFlow<List<Dispute>> = _disputes

    init {
        fetchDisputes()
    }

    private fun fetchDisputes() {
        viewModelScope.launch {
            val snapshot = db.collection("disputes").get().await()
            _disputes.value = snapshot.toObjects(Dispute::class.java)
        }
    }
}

