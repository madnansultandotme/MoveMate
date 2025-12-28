package com.application.movemate.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.movemate.models.Earnings
import com.application.movemate.models.EarningsSummary
import com.application.movemate.models.DailyEarning
import com.application.movemate.models.Transaction
import com.application.movemate.repositories.EarningsRepository
import com.application.movemate.repositories.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EarningsViewModel : ViewModel() {

    private val earningsRepository = EarningsRepository()
    private val transactionRepository = TransactionRepository()

    private val _earnings = MutableStateFlow<Earnings?>(null)
    val earnings: StateFlow<Earnings?> = _earnings.asStateFlow()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _earningsSummary = MutableStateFlow<EarningsSummary?>(null)
    val earningsSummary: StateFlow<EarningsSummary?> = _earningsSummary.asStateFlow()

    private val _weeklyEarnings = MutableStateFlow<List<DailyEarning>>(emptyList())
    val weeklyEarnings: StateFlow<List<DailyEarning>> = _weeklyEarnings.asStateFlow()

    private val _monthlyEarnings = MutableStateFlow<List<DailyEarning>>(emptyList())
    val monthlyEarnings: StateFlow<List<DailyEarning>> = _monthlyEarnings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun fetchEarnings(carrierId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            earningsRepository.getCarrierEarnings(carrierId)
                .onSuccess { _earnings.value = it }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun fetchTransactions(carrierId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            transactionRepository.getTransactionsByUser(carrierId).collect { transactions ->
                _transactions.value = transactions
            }
            _isLoading.value = false
        }
    }

    fun fetchWeeklyEarnings(carrierId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            earningsRepository.getWeeklyEarnings(carrierId)
                .onSuccess { _weeklyEarnings.value = it }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun fetchMonthlyEarnings(carrierId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            earningsRepository.getMonthlyEarnings(carrierId)
                .onSuccess { _monthlyEarnings.value = it }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun fetchEarningsSummary(carrierId: String, period: String, startDate: Long, endDate: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            earningsRepository.getEarningsSummary(carrierId, period, startDate, endDate)
                .onSuccess { _earningsSummary.value = it }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}
