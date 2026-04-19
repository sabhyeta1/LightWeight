package com.example.lightweight.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState(errorMessage = "Please fill in all fields")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            // TODO: wire to AuthService -> AuthRepository -> backend
            _uiState.value = AuthUiState(isSuccess = true)
        }
    }

    fun register(username: String, displayName: String, password: String, confirmPassword: String) {
        if (username.isBlank() || displayName.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState(errorMessage = "Please fill in all fields")
            return
        }
        if (password != confirmPassword) {
            _uiState.value = AuthUiState(errorMessage = "Passwords do not match")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            // TODO: wire to AuthService -> AuthRepository -> backend
            _uiState.value = AuthUiState(isSuccess = true)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}