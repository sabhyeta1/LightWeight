package com.example.lightweight.data.repository

import com.example.lightweight.data.remote.LoginRequest
import com.example.lightweight.data.remote.RegisterRequest
import com.example.lightweight.data.remote.RetrofitClient

class AuthRepository {

    suspend fun login(username: String, password: String): Result<String> {
        return try {
            val response = RetrofitClient.api.login(LoginRequest(username, password))
            Result.success(response.token)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(username: String, password: String, displayName: String): Result<String> {
        return try {
            val response = RetrofitClient.api.register(RegisterRequest(username, password, displayName))
            Result.success(response.token)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}