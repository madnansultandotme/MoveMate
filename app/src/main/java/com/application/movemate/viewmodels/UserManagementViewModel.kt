package com.application.movemate.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.movemate.models.User
import com.application.movemate.models.UserRole
import com.application.movemate.models.VerificationStatus
import com.application.movemate.models.Loader
import com.application.movemate.models.Carrier
import com.application.movemate.repositories.UserRepository
import com.application.movemate.repositories.LoaderRepository
import com.application.movemate.repositories.CarrierRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UserOperationState {
    object Idle : UserOperationState()
    object Loading : UserOperationState()
    object Success : UserOperationState()
    data class Error(val message: String) : UserOperationState()
}

data class CombinedUser(
    val user: User,
    val loader: Loader? = null,
    val carrier: Carrier? = null
)

class UserManagementViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val loaderRepository = LoaderRepository()
    private val carrierRepository = CarrierRepository()

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _loaders = MutableStateFlow<List<Loader>>(emptyList())
    val loaders: StateFlow<List<Loader>> = _loaders.asStateFlow()

    private val _carriers = MutableStateFlow<List<Carrier>>(emptyList())
    val carriers: StateFlow<List<Carrier>> = _carriers.asStateFlow()

    private val _selectedUser = MutableStateFlow<CombinedUser?>(null)
    val selectedUser: StateFlow<CombinedUser?> = _selectedUser.asStateFlow()

    private val _operationState = MutableStateFlow<UserOperationState>(UserOperationState.Idle)
    val operationState: StateFlow<UserOperationState> = _operationState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    init {
        fetchUsers()
        fetchLoaders()
        fetchCarriers()
    }

    fun fetchUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            userRepository.getAllUsers().collect { users ->
                _users.value = users
            }
            _isLoading.value = false
        }
    }

    fun fetchLoaders() {
        viewModelScope.launch {
            loaderRepository.getAllLoaders().collect { loaders ->
                _loaders.value = loaders
            }
        }
    }

    fun fetchCarriers() {
        viewModelScope.launch {
            carrierRepository.getAllCarriers().collect { carriers ->
                _carriers.value = carriers
            }
        }
    }

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
        when (filter) {
            "Loaders" -> fetchUsersByRole(UserRole.LOADER)
            "Carriers" -> fetchUsersByRole(UserRole.CARRIER)
            "Pending" -> fetchPendingUsers()
            else -> fetchUsers()
        }
    }

    fun fetchUsersByRole(role: UserRole) {
        viewModelScope.launch {
            _isLoading.value = true
            userRepository.getUsersByRole(role).collect { users ->
                _users.value = users
            }
            _isLoading.value = false
        }
    }

    fun fetchPendingUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            userRepository.getPendingVerificationUsers().collect { users ->
                _users.value = users
            }
            _isLoading.value = false
        }
    }

    fun fetchUserDetails(userId: String, role: UserRole) {
        viewModelScope.launch {
            _isLoading.value = true
            userRepository.getUser(userId)
                .onSuccess { user ->
                    user?.let { u ->
                        when (role) {
                            UserRole.LOADER -> {
                                loaderRepository.getLoader(userId).onSuccess { loader ->
                                    _selectedUser.value = CombinedUser(u, loader = loader)
                                }
                            }
                            UserRole.CARRIER -> {
                                carrierRepository.getCarrier(userId).onSuccess { carrier ->
                                    _selectedUser.value = CombinedUser(u, carrier = carrier)
                                }
                            }
                            UserRole.ADMIN -> {
                                _selectedUser.value = CombinedUser(u)
                            }
                        }
                    }
                }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun approveUser(userId: String, role: UserRole) {
        viewModelScope.launch {
            _operationState.value = UserOperationState.Loading
            val result = when (role) {
                UserRole.LOADER -> loaderRepository.updateVerificationStatus(userId, VerificationStatus.APPROVED)
                UserRole.CARRIER -> carrierRepository.updateVerificationStatus(userId, VerificationStatus.APPROVED)
                else -> Result.failure(Exception("Invalid role"))
            }

            result
                .onSuccess {
                    userRepository.updateUser(userId, mapOf("verificationStatus" to VerificationStatus.APPROVED.name))
                    _operationState.value = UserOperationState.Success
                    fetchUsers()
                }
                .onFailure {
                    _operationState.value = UserOperationState.Error(it.message ?: "Failed to approve user")
                }
        }
    }

    fun rejectUser(userId: String, role: UserRole, reason: String) {
        viewModelScope.launch {
            _operationState.value = UserOperationState.Loading
            val result = when (role) {
                UserRole.LOADER -> loaderRepository.updateVerificationStatus(userId, VerificationStatus.REJECTED, reason)
                UserRole.CARRIER -> carrierRepository.updateVerificationStatus(userId, VerificationStatus.REJECTED, reason)
                else -> Result.failure(Exception("Invalid role"))
            }

            result
                .onSuccess {
                    userRepository.updateUser(userId, mapOf(
                        "verificationStatus" to VerificationStatus.REJECTED.name
                    ))
                    _operationState.value = UserOperationState.Success
                    fetchUsers()
                }
                .onFailure {
                    _operationState.value = UserOperationState.Error(it.message ?: "Failed to reject user")
                }
        }
    }

    fun suspendUser(userId: String, role: UserRole, reason: String) {
        viewModelScope.launch {
            _operationState.value = UserOperationState.Loading
            val result = when (role) {
                UserRole.LOADER -> loaderRepository.suspendLoader(userId, reason)
                UserRole.CARRIER -> carrierRepository.suspendCarrier(userId, reason)
                else -> Result.failure(Exception("Invalid role"))
            }

            result
                .onSuccess {
                    userRepository.suspendUser(userId, reason)
                    _operationState.value = UserOperationState.Success
                    fetchUsers()
                }
                .onFailure {
                    _operationState.value = UserOperationState.Error(it.message ?: "Failed to suspend user")
                }
        }
    }

    fun reactivateUser(userId: String, role: UserRole) {
        viewModelScope.launch {
            _operationState.value = UserOperationState.Loading
            val result = when (role) {
                UserRole.CARRIER -> carrierRepository.reactivateCarrier(userId)
                else -> userRepository.reactivateUser(userId)
            }

            result
                .onSuccess {
                    _operationState.value = UserOperationState.Success
                    fetchUsers()
                }
                .onFailure {
                    _operationState.value = UserOperationState.Error(it.message ?: "Failed to reactivate user")
                }
        }
    }

    fun searchUsers(query: String): List<User> {
        return _users.value.filter { user ->
            user.name.contains(query, ignoreCase = true) ||
            user.email.contains(query, ignoreCase = true) ||
            user.id.contains(query, ignoreCase = true)
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun resetOperationState() {
        _operationState.value = UserOperationState.Idle
    }
}
