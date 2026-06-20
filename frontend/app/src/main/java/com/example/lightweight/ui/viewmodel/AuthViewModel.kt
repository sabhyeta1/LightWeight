package com.example.lightweight.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lightweight.data.local.TokenStore
import com.example.lightweight.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.lightweight.util.*

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class AuthViewModel (application: Application) : AndroidViewModel(application){ //: ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    // hinzugefügt
    private val repository = AuthRepository()
    private val tokenStore = TokenStore(application)

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState(errorMessage = "Please fill in all fields")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            // TODO: wire to AuthService -> AuthRepository -> backend
            repository.login(username, password)
                .onSuccess { token ->
                    tokenStore.saveToken(token)
                    _uiState.value = AuthUiState(isSuccess = true)
                }
                .onFailure { error ->
                    val message = when {
                        error.httpStatusOrNull() == 401 -> "Invalid username or password."
                        error.isNetworkError() -> "No internet connection. Please check your network and try again."
                        else -> "We couldn't log you in. Please try again."
                    }
                    _uiState.value = AuthUiState(errorMessage = message)
                }
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
            repository.register(username, password, displayName)
                .onSuccess { token ->
                    tokenStore.saveToken(token)
                    _uiState.value = AuthUiState(isSuccess = true)
                }
                .onFailure { error ->
                    val message = when {
                        error.isNetworkError() -> "No internet connection. Please check your network and try again."
                        error.httpStatusOrNull() == 409 -> "This username is already taken. Please choose another one."
                        error.httpStatusOrNull() == 400 -> "Please check your details and try again."
                        else -> "We couldn't create your account. Please try again."
                    }
                    _uiState.value = AuthUiState(errorMessage = message)
                }

            //_uiState.value = AuthUiState(isSuccess = true)
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenStore.clearToken()
            _uiState.value = AuthUiState()
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}