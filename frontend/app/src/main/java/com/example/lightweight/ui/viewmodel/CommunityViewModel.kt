package com.example.lightweight.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lightweight.data.local.TokenStore
import com.example.lightweight.data.remote.CommunityPlanResponse
import com.example.lightweight.data.remote.WorkoutPlanDetailResponse
import com.example.lightweight.data.repository.CommunityRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class CommunityUiState(
    val isLoading: Boolean = false,
    val plans: List<CommunityPlanResponse> = emptyList(),
    val errorMessage: String? = null,
    val copySuccessMessage: String? = null,
    val isLoadingDetails: Boolean = false,
    val selectedPlanDetails: WorkoutPlanDetailResponse? = null
)

class CommunityViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CommunityRepository()
    private val tokenStore = TokenStore(application)

    private val _uiState = MutableStateFlow(CommunityUiState())
    val uiState: StateFlow<CommunityUiState> = _uiState

    // speichert den laufende suche damit er bei neuer Eingabe gecancelt werden kann
    private var searchJob: Job? = null

    init {
        loadPlans()
    }

    fun loadPlans(search: String = "", filterType: String = "name") {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val token = tokenStore.getToken().first()
            if (token == null) {
                _uiState.value = _uiState.value.copy(isLoading = false, plans = emptyList())
                return@launch
            }
            repository.getCommunityPlans(token, search, filterType)
                .onSuccess { plans ->
                    _uiState.value = _uiState.value.copy(isLoading = false, plans = plans)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = error.message)
                }
        }
    }

    // wird bei jeder änderung in suchzeile aufgerufen
    // cancelt vorherige suche und wartet 400ms bevor ein neuer request abgeht
    fun onSearchChanged(query: String, filterType: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400)
            loadPlans(query, filterType)
        }
    }

    fun copyPlan(planId: Int) {
        viewModelScope.launch {
            val token = tokenStore.getToken().first() ?: return@launch
            repository.copyPlan(token, planId)
                .onSuccess { plan ->
                    _uiState.value = _uiState.value.copy(
                        copySuccessMessage = "\"${plan.name}\" was added to your plans"
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(errorMessage = error.message)
                }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, copySuccessMessage = null)
    }

    fun loadPlanDetails(planId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingDetails = true, selectedPlanDetails = null)
            val token = tokenStore.getToken().first() ?: return@launch
            repository.getCommunityPlanDetails(token, planId)
                .onSuccess { plan ->
                    _uiState.value = _uiState.value.copy(isLoadingDetails = false, selectedPlanDetails = plan)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(isLoadingDetails = false, errorMessage = error.message)
                }
        }
    }
}