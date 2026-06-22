package com.example.lightweight.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lightweight.data.local.TokenStore
import com.example.lightweight.data.remote.SupplementResponse
import com.example.lightweight.data.repository.SupplementRepository
import com.example.lightweight.util.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class SupplementUiState(
    val isLoading: Boolean = false,
    val supplements: List<SupplementResponse> = emptyList(),
    val errorMessage: String? = null
)

class SupplementViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SupplementRepository()
    private val tokenStore = TokenStore(application)

    private val _uiState = MutableStateFlow(SupplementUiState())
    val uiState: StateFlow<SupplementUiState> = _uiState

    init {
        loadSupplements()
    }

    fun loadSupplements() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val token = tokenStore.getToken().first() ?: return@launch
            repository.getSupplements(token)
                .onSuccess { supplements ->
                    _uiState.value = _uiState.value.copy(isLoading = false, supplements = supplements)
                }
                .onFailure { error ->
                    val message = if (error.isNetworkError()) {
                        "No internet connection. Please check your network and try again."
                    } else {
                        "We couldn't load your supplements. Please try again."
                    }
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = message)
                }
        }
    }

    fun addSupplement(name: String, dosage: String) {
        if (name.isBlank() || dosage.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter both name and dosage")
            return
        }
        viewModelScope.launch {
            val token = tokenStore.getToken().first() ?: return@launch
            repository.createSupplement(token, name.trim(), dosage.trim())
                .onSuccess {
                    loadSupplements()
                }
                .onFailure { error ->
                    val message = when {
                        error.isNetworkError() -> "No internet connection. Please check your network and try again."
                        error.httpStatusOrNull() == 400 -> "Please check the name and dosage."
                        else -> "We couldn't add this supplement. Please try again."
                    }
                    _uiState.value = _uiState.value.copy(errorMessage = message)
                }
        }
    }

    fun deleteSupplement(id: Int) {
        viewModelScope.launch {
            val token = tokenStore.getToken().first() ?: return@launch
            repository.deleteSupplement(token, id)
                .onSuccess { loadSupplements() }
                .onFailure { error ->
                    val message = when {
                        error.isNetworkError() -> "No internet connection. Please check your network and try again."
                        error.httpStatusOrNull() == 404 -> "This supplement no longer exists."
                        else -> "We couldn't remove this supplement. Please try again."
                    }
                    _uiState.value = _uiState.value.copy(errorMessage = message)
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}