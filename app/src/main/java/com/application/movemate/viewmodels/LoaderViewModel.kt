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

class LoaderViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val _loader = MutableStateFlow<Loader?>(null)
    val loader: StateFlow<Loader?> = _loader

    fun fetchLoader(loaderId: String) {
        viewModelScope.launch {
            val snapshot = db.collection("loaders").document(loaderId).get().await()
            _loader.value = snapshot.toObject(Loader::class.java)
        }
    }
}

