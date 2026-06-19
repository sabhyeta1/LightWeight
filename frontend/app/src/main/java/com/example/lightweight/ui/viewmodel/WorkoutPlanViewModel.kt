package com.example.lightweight.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lightweight.data.local.TokenStore
import com.example.lightweight.data.remote.CreateWorkoutPlanRequest
import com.example.lightweight.data.remote.ExerciseSetInput
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

data class DraftExercise(
    val exerciseId: Int,
    val name: String,
    val sets: List<ExerciseSetInput> = emptyList()
)

data class DraftPlanState(
    val name: String = "",
    val description: String = "",
    val isPublic: Boolean = false,
    val initialized: Boolean = false,
    val selectedExercises: List<DraftExercise> = emptyList()
)

class WorkoutPlanViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WorkoutPlanRepository()
    private val tokenStore = TokenStore(application)

    private val _uiState = MutableStateFlow(WorkoutPlanUiState())
    val uiState: StateFlow<WorkoutPlanUiState> = _uiState

    private val _draftPlan = MutableStateFlow(DraftPlanState())
    val draftPlan: StateFlow<DraftPlanState> = _draftPlan

    //hinzugefügt
    private var originalEwpIds: List<Int> = emptyList()

    init {
        loadPlans()
    }

    fun createPlan(name: String, description: String, isPublic: Boolean) {
        viewModelScope.launch {
            val token = tokenStore.getToken().first() ?: return@launch

            val planResult = repository.createWorkoutPlan(token, name, description, isPublic)
            val plan = planResult.getOrElse { error ->
                _uiState.value = _uiState.value.copy(errorMessage = error.message)
                return@launch
            }

            _draftPlan.value.selectedExercises.forEachIndexed { index, draftExercise ->
                val addResult = repository.addExerciseToPlan(token, plan.id, draftExercise.exerciseId, index + 1)
                val addedExercise = addResult.getOrNull()
                if (addedExercise != null && draftExercise.sets.isNotEmpty()) {
                    repository.updateExerciseSets(token, plan.id, addedExercise.id, draftExercise.sets)
                }
            }

            clearDraftPlan()
            loadPlans()
        }
    }

    /*fun createPlan(name: String, description: String, isPublic: Boolean) {
        viewModelScope.launch {
            val token = tokenStore.getToken().first() ?: return@launch
            repository.createWorkoutPlan(token, name, description, isPublic)
                .onSuccess { loadPlans() }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(errorMessage = error.message)
                }
        }
    }*/

    fun initDraftIfNeeded(name: String, description: String, isPublic: Boolean) {
        if (!_draftPlan.value.initialized) {
            _draftPlan.value = _draftPlan.value.copy(
                name = name,
                description = description,
                isPublic = isPublic,
                initialized = true
            )
        }
    }

    fun updateDraftName(name: String) {
        _draftPlan.value = _draftPlan.value.copy(name = name)
    }

    fun updateDraftDescription(description: String) {
        _draftPlan.value = _draftPlan.value.copy(description = description)
    }

    fun updateDraftIsPublic(isPublic: Boolean) {
        _draftPlan.value = _draftPlan.value.copy(isPublic = isPublic)
    }
    fun toggleExerciseSelection(exerciseId: Int, name: String) {
        val current = _draftPlan.value.selectedExercises
        val alreadySelected = current.any { it.exerciseId == exerciseId }
        _draftPlan.value = _draftPlan.value.copy(
            selectedExercises = if (alreadySelected) {
                current.filter { it.exerciseId != exerciseId }
            } else {
                current + DraftExercise(exerciseId = exerciseId, name = name)
            }
        )
    }

    fun isExerciseSelected(exerciseId: Int): Boolean {
        return _draftPlan.value.selectedExercises.any { it.exerciseId == exerciseId }
    }

    fun updateSetsForExercise(exerciseId: Int, sets: List<ExerciseSetInput>) {
        _draftPlan.value = _draftPlan.value.copy(
            selectedExercises = _draftPlan.value.selectedExercises.map {
                if (it.exerciseId == exerciseId) it.copy(sets = sets) else it
            }
        )
    }

    fun getSetsForExercise(exerciseId: Int): List<ExerciseSetInput> {
        return _draftPlan.value.selectedExercises.firstOrNull { it.exerciseId == exerciseId }?.sets ?: emptyList()
    }

    fun clearDraftPlan() {
        _draftPlan.value = DraftPlanState()
    }

    //hinzugefügt
    fun loadPlanIntoDraft(planId: Int) {
        if (_draftPlan.value.initialized) return  // schon geladen — nicht erneut überschreiben
        viewModelScope.launch {
            val token = tokenStore.getToken().first() ?: return@launch
            repository.getWorkoutPlanDetails(token, planId)
                .onSuccess { details ->
                    originalEwpIds = details.exercises.map { it.ewp_id }
                    val draftExercises = details.exercises.map { ex ->
                        DraftExercise(
                            exerciseId = ex.exercise_id,
                            name = ex.name,
                            sets = ex.sets.map { s ->
                                ExerciseSetInput(
                                    set_number = s.set_number ?: 1,
                                    reps = s.reps,
                                    weight = s.weight,
                                    machine_settings = s.machine_settings,
                                    is_drop_set = s.is_drop_set ?: false
                                )
                            }
                        )
                    }
                    _draftPlan.value = DraftPlanState(
                        name = details.name,
                        description = details.description ?: "",
                        isPublic = details.is_published,
                        initialized = true,
                        selectedExercises = draftExercises
                    )
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

            val updateResult = repository.updateWorkoutPlan(token, planId, name, description, isPublic)
            if (updateResult.isFailure) {
                _uiState.value = _uiState.value.copy(errorMessage = updateResult.exceptionOrNull()?.message)
                return@launch
            }

            // alte Exercise-Zuordnungen entfernen, dann die aktuell ausgewählten neu anlegen
            originalEwpIds.forEach { ewpId ->
                repository.removeExerciseFromPlan(token, planId, ewpId)
            }

            _draftPlan.value.selectedExercises.forEachIndexed { index, draftExercise ->
                val addResult = repository.addExerciseToPlan(token, planId, draftExercise.exerciseId, index + 1)
                val addedExercise = addResult.getOrNull()
                if (addedExercise != null && draftExercise.sets.isNotEmpty()) {
                    repository.updateExerciseSets(token, planId, addedExercise.id, draftExercise.sets)
                }
            }

            originalEwpIds = emptyList()
            clearDraftPlan()
            loadPlans()
        }
    }
    /*fun updatePlan(planId: Int, name: String, description: String, isPublic: Boolean) {
        viewModelScope.launch {
            val token = tokenStore.getToken().first() ?: return@launch
            repository.updateWorkoutPlan(token, planId, name, description, isPublic)
                .onSuccess { loadPlans() }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(errorMessage = error.message)
                }
        }
    }*/

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
