package com.example.lightweight.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lightweight.data.local.TokenStore
import com.example.lightweight.data.remote.UserProfileResponse
import com.example.lightweight.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = false,
    val profile: UserProfileResponse? = null,
    val errorMessage: String? = null,
    val saveSuccess: Boolean = false
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProfileRepository()
    private val tokenStore = TokenStore(application)

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    fun loadProfile() {
        viewModelScope.launch {
            val token = tokenStore.getToken().first() ?: return@launch
            _uiState.value = ProfileUiState(isLoading = true)
            repository.getProfile(token)
                .onSuccess { profile ->
                    _uiState.value = ProfileUiState(profile = profile)
                }
                .onFailure { error ->
                    _uiState.value = ProfileUiState(errorMessage = error.message ?: "Failed to load profile")
                }
        }
    }

    fun saveProfile(displayName: String, profilePictureUrl: String?) {
        if (displayName.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Display name cannot be empty")
            return
        }
        viewModelScope.launch {
            val token = tokenStore.getToken().first() ?: return@launch
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, saveSuccess = false)
            repository.updateProfile(token, displayName.trim(), profilePictureUrl?.ifBlank { null })
                .onSuccess { updated ->
                    _uiState.value = ProfileUiState(profile = updated, saveSuccess = true)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to save profile"
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }
}