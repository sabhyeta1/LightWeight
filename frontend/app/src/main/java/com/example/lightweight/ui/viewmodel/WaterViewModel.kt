package com.example.lightweight.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lightweight.data.local.TokenStore
import com.example.lightweight.data.remote.WaterLogResponse
import com.example.lightweight.data.repository.WaterRepository
import com.example.lightweight.util.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

const val GLASS_SIZE_ML = 250

data class WaterUiState(
    val isLoading: Boolean = false,
    val targetMl: Int? = null,
    val logs: List<WaterLogResponse> = emptyList(),
    val errorMessage: String? = null
) {
    val totalMl: Int get() = logs.sumOf { it.amount_ml }
}

class WaterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WaterRepository()
    private val tokenStore = TokenStore(application)

    private val _uiState = MutableStateFlow(WaterUiState())
    val uiState: StateFlow<WaterUiState> = _uiState

    init {
        loadStatus()
    }

    fun loadStatus() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val token = tokenStore.getToken().first() ?: return@launch
            repository.getStatus(token)
                .onSuccess { status ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        targetMl = status.target_ml,
                        logs = status.logs
                    )
                }
                .onFailure { error ->
                    val message = if (error.isNetworkError()) {
                        "No internet connection. Please check your network and try again."
                    } else {
                        "We couldn't load your water tracking data. Please try again."
                    }
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = message)
                }
        }
    }

    fun setGoal(targetMl: Int) {
        if (targetMl <= 0) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a target greater than 0")
            return
        }
        viewModelScope.launch {
            val token = tokenStore.getToken().first() ?: return@launch
            repository.setGoal(token, targetMl)
                .onSuccess { goal ->
                    _uiState.value = _uiState.value.copy(targetMl = goal.target_ml)
                }
                .onFailure { error ->
                    val message = when {
                        error.isNetworkError() -> "No internet connection. Please check your network and try again."
                        error.httpStatusOrNull() == 400 -> "Please enter a valid target in millilitres."
                        else -> "We couldn't save your goal. Please try again."
                    }
                    _uiState.value = _uiState.value.copy(errorMessage = message)
                }
        }
    }

    fun addGlass() {
        addIntake(GLASS_SIZE_ML)
    }

    fun addIntake(amountMl: Int) {
        viewModelScope.launch {
            val token = tokenStore.getToken().first() ?: return@launch
            repository.addIntake(token, amountMl)
                .onSuccess {
                    loadStatus()
                }
                .onFailure { error ->
                    val message = if (error.isNetworkError()) {
                        "No internet connection. Please check your network and try again."
                    } else {
                        "We couldn't log your water intake. Please try again."
                    }
                    _uiState.value = _uiState.value.copy(errorMessage = message)
                }
        }
    }

    fun deleteIntake(logId: Int) {
        viewModelScope.launch {
            val token = tokenStore.getToken().first() ?: return@launch
            repository.deleteIntake(token, logId)
                .onSuccess {
                    loadStatus()
                }
                .onFailure { error ->
                    val message = when {
                        error.isNetworkError() -> "No internet connection. Please check your network and try again."
                        error.httpStatusOrNull() == 404 -> "This entry no longer exists."
                        else -> "We couldn't remove this entry. Please try again."
                    }
                    _uiState.value = _uiState.value.copy(errorMessage = message)
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}