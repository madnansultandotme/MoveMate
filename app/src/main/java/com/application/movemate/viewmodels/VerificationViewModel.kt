package com.application.movemate.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.movemate.repositories.VerificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VerificationViewModel : ViewModel() {

    private val repository = VerificationRepository()

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun uploadDocument(carrierId: String, documentName: String, documentUrl: String) {
        viewModelScope.launch {
            _isUploading.value = true
            val result = repository.uploadDocumentUrl(carrierId, documentName, documentUrl)
            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message
            }
            _isUploading.value = false
        }
    }
}

