package com.example.lightweight.data.repository

import com.example.lightweight.data.remote.RetrofitClient
import com.example.lightweight.data.remote.UpdateProfileRequest
import com.example.lightweight.data.remote.UserProfileResponse

class ProfileRepository {

    suspend fun getProfile(token: String): Result<UserProfileResponse> {
        return try {
            val response = RetrofitClient.api.getProfile("Bearer $token")
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(
        token: String,
        displayName: String,
        profilePictureUrl: String?
    ): Result<UserProfileResponse> {
        return try {
            val response = RetrofitClient.api.updateProfile(
                token = "Bearer $token",
                body = UpdateProfileRequest(
                    display_name = displayName,
                    profile_picture_url = profilePictureUrl
                )
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}