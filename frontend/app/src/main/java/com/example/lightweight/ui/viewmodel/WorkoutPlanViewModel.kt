package com.example.lightweight.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lightweight.data.local.TokenStore
import com.example.lightweight.data.remote.CreateWorkoutPlanRequest
import com.example.lightweight.data.remote.RetrofitClient
import com.example.lightweight.data.remote.WorkoutPlanResponse
import com.example.lightweight.data.remote.WorkoutPlanDetailResponse
import com.example.lightweight.data.repository.WorkoutPlanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class WorkoutPlanUiState(
    val isLoading: Boolean = false,
    val plans: List<WorkoutPlanResponse> = emptyList(),
    val currentPlanDetails: WorkoutPlanDetailResponse? = null,
    val errorMessage: String? = null
)

class WorkoutPlanViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WorkoutPlanRepository()
    private val tokenStore = TokenStore(application)

    private val _uiState = MutableStateFlow(WorkoutPlanUiState())
    val uiState: StateFlow<WorkoutPlanUiState> = _uiState

    init {
        loadPlans()
    }

    fun createPlan(name: String, description: String, isPublic: Boolean) {
        viewModelScope.launch {
            val token = tokenStore.getToken().first() ?: return@launch
            repository.createWorkoutPlan(token, name, description, isPublic)
                .onSuccess { loadPlans() }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(errorMessage = error.message)
                }
        }
    }

    fun loadPlans() {
        viewModelScope.launch {
            _uiState.value = WorkoutPlanUiState(isLoading = true)
            val token = tokenStore.getToken().first()
            if (token == null) {
                _uiState.value = WorkoutPlanUiState(plans = emptyList())
                return@launch
            }
            repository.getWorkoutPlans(token)
                .onSuccess { plans ->
                    _uiState.value = WorkoutPlanUiState(plans = plans)
                }
                .onFailure { error ->
                    _uiState.value = WorkoutPlanUiState(plans = emptyList())
                }
        }
    }

    fun deletePlan(id: Int) {
        viewModelScope.launch {
            val token = tokenStore.getToken().first() ?: return@launch
            repository.deleteWorkoutPlan(token, id)
                .onSuccess { loadPlans() }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(errorMessage = error.message)
                }
        }
    }

    fun updatePlan(planId: Int, name: String, description: String, isPublic: Boolean) {
        viewModelScope.launch {
            val token = tokenStore.getToken().first() ?: return@launch
            repository.updateWorkoutPlan(token, planId, name, description, isPublic)
                .onSuccess { loadPlans() }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(errorMessage = error.message)
                }
        }
    }

    fun loadPlanDetails(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, currentPlanDetails = null)
            val token = tokenStore.getToken().first() ?: return@launch
            repository.getWorkoutPlanDetails(token, id)
                .onSuccess { details ->
                    _uiState.value = _uiState.value.copy(isLoading = false, currentPlanDetails = details)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = error.message)
                }
        }
    }

    fun getPlanById(id: Int): WorkoutPlanResponse? {
        return _uiState.value.plans.firstOrNull { it.id == id }
    }

    fun clearPlans() {
        _uiState.value = WorkoutPlanUiState(plans = emptyList())
    }
}
