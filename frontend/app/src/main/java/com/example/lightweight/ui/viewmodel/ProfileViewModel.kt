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
import com.example.lightweight.util.*

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
                    val message = if (error.isNetworkError()) {
                        "No internet connection. Please check your network and try again."
                    } else {
                        "We couldn't load your profile. Please try again."
                    }
                    _uiState.value = ProfileUiState(errorMessage = message)
                }
        }
    }

    fun saveProfile(displayName: String) {
        if (displayName.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Display name cannot be empty")
            return
        }
        viewModelScope.launch {
            val token = tokenStore.getToken().first() ?: return@launch
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, saveSuccess = false)
            repository.updateProfile(token, displayName.trim())
                .onSuccess { updated ->
                    _uiState.value = ProfileUiState(profile = updated, saveSuccess = true)
                }
                .onFailure { error ->
                    val message = when {
                        error.isNetworkError() -> "No internet connection. Please check your network and try again."
                        error.httpStatusOrNull() == 400 -> "Please check your display name and try again."
                        else -> "We couldn't save your profile. Please try again."
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = message
                    )
                }
        }
    }

    fun uploadProfilePicture(imagePart: okhttp3.MultipartBody.Part) {
        viewModelScope.launch {
            val token = tokenStore.getToken().first() ?: return@launch

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                saveSuccess = false
            )

            repository.uploadProfilePicture(token, imagePart)
                .onSuccess { updated ->
                    _uiState.value = ProfileUiState(
                        profile = updated,
                        saveSuccess = true
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "We couldn't upload your profile picture. Please try again."
                    )
                }
        }
    }

    fun deleteProfilePicture() {
        viewModelScope.launch {
            val token = tokenStore.getToken().first() ?: return@launch

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                saveSuccess = false
            )

            repository.deleteProfilePicture(token)
                .onSuccess { updated ->
                    _uiState.value = ProfileUiState(
                        profile = updated,
                        saveSuccess = true
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "We couldn't delete your profile picture. Please try again."
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