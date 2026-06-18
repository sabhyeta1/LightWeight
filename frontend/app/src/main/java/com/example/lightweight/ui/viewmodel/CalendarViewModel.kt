package com.example.lightweight.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lightweight.data.local.TokenStore
import com.example.lightweight.data.remote.CalendarSessionResponse
import com.example.lightweight.data.remote.CreateRecurrenceRequest
import com.example.lightweight.data.remote.CreateSessionRequest
import com.example.lightweight.data.remote.ExerciseLibraryResponse
import com.example.lightweight.data.remote.MuscleGroupResponse
import com.example.lightweight.data.repository.CalendarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class CalendarUiState(
    val isLoading: Boolean = false,
    val sessions: List<CalendarSessionResponse> = emptyList(),
    // How far ahead the schedule list has loaded (used for "load more")
    val loadedUntil: LocalDate = LocalDate.now().plusMonths(3),
    val errorMessage: String? = null,
    val successMessage: String? = null
)

data class ExerciseLibraryUiState(
    val isLoading: Boolean = false,
    val exercises: List<ExerciseLibraryResponse> = emptyList(),
    val muscleGroups: List<MuscleGroupResponse> = emptyList(),
    val selectedMuscleGroupId: Int? = null,
    val searchQuery: String = "",
    val errorMessage: String? = null
)

class CalendarViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CalendarRepository()
    private val tokenStore = TokenStore(application)

    private val _calendarState = MutableStateFlow(CalendarUiState())
    val calendarState: StateFlow<CalendarUiState> = _calendarState

    private val _libraryState = MutableStateFlow(ExerciseLibraryUiState())
    val libraryState: StateFlow<ExerciseLibraryUiState> = _libraryState

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    init {
        loadSchedule()
    }

    // Schedule (FR-12, FR-15)

    /**
     * Load (or reload) sessions from today through [until].
     * Called on init, after any mutation, and when the user wants to see more.
     */
    fun loadSchedule(until: LocalDate = _calendarState.value.loadedUntil) {
        viewModelScope.launch {
            _calendarState.value = _calendarState.value.copy(isLoading = true, errorMessage = null)
            val token = tokenStore.getToken().first() ?: return@launch

            val from = LocalDate.now().format(dateFormatter)
            val to   = until.format(dateFormatter)

            repository.getSessions(token, from, to)
                .onSuccess { sessions ->
                    _calendarState.value = _calendarState.value.copy(
                        isLoading = false,
                        sessions = sessions,
                        loadedUntil = until
                    )
                }
                .onFailure { error ->
                    _calendarState.value = _calendarState.value.copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
        }
    }

    /** Extend the window by another 3 months ("load more"). */
    fun loadMore() {
        loadSchedule(_calendarState.value.loadedUntil.plusMonths(3))
    }

    fun scheduleSession(workoutPlanId: Int, date: LocalDate, time: String, colorId: Int) {
        viewModelScope.launch {
            val token = tokenStore.getToken().first() ?: return@launch
            val request = CreateSessionRequest(
                workout_plan_id = workoutPlanId,
                session_date = date.format(dateFormatter),
                session_time = time,
                color_id = colorId
            )
            repository.createSession(token, request)
                .onSuccess {
                    _calendarState.value = _calendarState.value.copy(successMessage = "Session scheduled")
                    loadSchedule()
                }
                .onFailure { error ->
                    _calendarState.value = _calendarState.value.copy(errorMessage = error.message)
                }
        }
    }

    fun deleteSession(sessionId: Int) {
        viewModelScope.launch {
            val token = tokenStore.getToken().first() ?: return@launch
            repository.deleteSession(token, sessionId)
                .onSuccess { loadSchedule() }
                .onFailure { error ->
                    _calendarState.value = _calendarState.value.copy(errorMessage = error.message)
                }
        }
    }

    fun scheduleRecurrence(
        workoutPlanId: Int,
        type: String,
        weekdays: List<Int>?,
        intervalDays: Int?,
        startDate: LocalDate,
        endDate: LocalDate,
        time: String,
        colorId: Int
    ) {
        viewModelScope.launch {
            val token = tokenStore.getToken().first() ?: return@launch
            val request = CreateRecurrenceRequest(
                workout_plan_id = workoutPlanId,
                type = type,
                weekdays = weekdays,
                interval_days = intervalDays,
                start_date = startDate.format(dateFormatter),
                end_date = endDate.format(dateFormatter),
                session_time = time,
                color_id = colorId
            )
            repository.createRecurrence(token, request)
                .onSuccess { result ->
                    _calendarState.value = _calendarState.value.copy(
                        successMessage = "${result.sessions.size} sessions scheduled"
                    )
                    // Extend loaded window to cover the recurrence end date if needed
                    val newUntil = maxOf(_calendarState.value.loadedUntil, endDate)
                    loadSchedule(newUntil)
                }
                .onFailure { error ->
                    _calendarState.value = _calendarState.value.copy(errorMessage = error.message)
                }
        }
    }

    fun deleteRecurrence(recurrenceId: Int) {
        viewModelScope.launch {
            val token = tokenStore.getToken().first() ?: return@launch
            repository.deleteRecurrence(token, recurrenceId)
                .onSuccess { loadSchedule() }
                .onFailure { error ->
                    _calendarState.value = _calendarState.value.copy(errorMessage = error.message)
                }
        }
    }

    fun clearMessages() {
        _calendarState.value = _calendarState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }

    // Exercise Library (FR-16)

    fun loadExerciseLibrary() {
        viewModelScope.launch {
            _libraryState.value = _libraryState.value.copy(isLoading = true, errorMessage = null)
            val token = tokenStore.getToken().first() ?: return@launch

            repository.getMuscleGroups(token)
                .onSuccess { groups ->
                    _libraryState.value = _libraryState.value.copy(muscleGroups = groups)
                }

            val selectedId = _libraryState.value.selectedMuscleGroupId
            repository.getExercises(token, selectedId)
                .onSuccess { exercises ->
                    _libraryState.value = _libraryState.value.copy(
                        isLoading = false,
                        exercises = exercises
                    )
                }
                .onFailure { error ->
                    _libraryState.value = _libraryState.value.copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
        }
    }

    fun filterByMuscleGroup(muscleGroupId: Int?) {
        _libraryState.value = _libraryState.value.copy(selectedMuscleGroupId = muscleGroupId)
        viewModelScope.launch {
            val token = tokenStore.getToken().first() ?: return@launch
            repository.getExercises(token, muscleGroupId)
                .onSuccess { exercises ->
                    _libraryState.value = _libraryState.value.copy(exercises = exercises)
                }
        }
    }

    fun setSearchQuery(query: String) {
        _libraryState.value = _libraryState.value.copy(searchQuery = query)
    }

    fun getFilteredExercises(): List<ExerciseLibraryResponse> {
        val query = _libraryState.value.searchQuery.trim().lowercase()
        if (query.isEmpty()) return _libraryState.value.exercises
        return _libraryState.value.exercises.filter { it.name.lowercase().contains(query) }
    }
}