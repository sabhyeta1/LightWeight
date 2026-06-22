package com.example.lightweight.data.repository

import com.example.lightweight.data.remote.RetrofitClient
import com.example.lightweight.data.remote.UpdateProfileRequest
import com.example.lightweight.data.remote.UserProfileResponse
import okhttp3.MultipartBody

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
    ): Result<UserProfileResponse> {
        return try {
            val response = RetrofitClient.api.updateProfile(
                token = "Bearer $token",
                body = UpdateProfileRequest(
                    display_name = displayName
                )
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadProfilePicture(
        token: String,
        imagePart: MultipartBody.Part
    ): Result<UserProfileResponse> {
        return try {
            val response = RetrofitClient.api.uploadProfilePicture(
                token = "Bearer $token",
                image = imagePart
            )

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProfilePicture(token: String): Result<UserProfileResponse> {
        return try {
            val response = RetrofitClient.api.deleteProfilePicture(
                token = "Bearer $token"
            )

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}