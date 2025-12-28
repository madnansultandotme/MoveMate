package com.application.movemate.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.movemate.models.Transaction
import com.application.movemate.models.TransactionStatus
import com.application.movemate.models.TransactionType
import com.application.movemate.repositories.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FinancialStats(
    val totalVolume: Double = 0.0,
    val netRevenue: Double = 0.0,
    val pendingPayouts: Double = 0.0,
    val refundsProcessed: Double = 0.0,
    val volumeGrowthPercentage: Double = 0.0,
    val revenueGrowthPercentage: Double = 0.0,
    val pendingPayoutCarriers: Int = 0,
    val refundLoaders: Int = 0,
    val weeklyRevenue: List<Double> = emptyList()
)

class FinancialViewModel : ViewModel() {

    private val transactionRepository = TransactionRepository()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _financialStats = MutableStateFlow(FinancialStats())
    val financialStats: StateFlow<FinancialStats> = _financialStats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _selectedPeriod = MutableStateFlow("This Month")
    val selectedPeriod: StateFlow<String> = _selectedPeriod.asStateFlow()

    init {
        fetchTransactions()
        fetchFinancialStats()
    }

    fun setSelectedPeriod(period: String) {
        _selectedPeriod.value = period
        fetchTransactions()
        fetchFinancialStats()
    }

    private fun fetchTransactions() {
        viewModelScope.launch {
            _isLoading.value = true
            transactionRepository.getRecentTransactions(20).collect { transactions ->
                _transactions.value = transactions
            }
            _isLoading.value = false
        }
    }

    private fun fetchFinancialStats() {
        viewModelScope.launch {
            transactionRepository.getFinancialStats()
                .onSuccess { stats ->
                    _financialStats.value = stats
                }
                .onFailure {
                    _error.value = it.message
                }
        }
    }

    fun getTransactionsByType(type: TransactionType): List<Transaction> {
        return _transactions.value.filter { it.type == type }
    }

    fun getTransactionsByStatus(status: TransactionStatus): List<Transaction> {
        return _transactions.value.filter { it.status == status }
    }

    fun refreshData() {
        fetchTransactions()
        fetchFinancialStats()
    }

    fun clearError() {
        _error.value = null
    }
}
