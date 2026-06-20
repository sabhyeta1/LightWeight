package com.example.lightweight.data.repository

import com.example.lightweight.data.remote.CreateSupplementRequest
import com.example.lightweight.data.remote.RetrofitClient
import com.example.lightweight.data.remote.SupplementResponse

class SupplementRepository {

    suspend fun getSupplements(token: String): Result<List<SupplementResponse>> {
        return try {
            val supplements = RetrofitClient.api.getSupplements("Bearer $token")
            Result.success(supplements)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createSupplement(token: String, name: String, dosage: String): Result<SupplementResponse> {
        return try {
            val supplement = RetrofitClient.api.createSupplement("Bearer $token", CreateSupplementRequest(name, dosage))
            Result.success(supplement)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteSupplement(token: String, id: Int): Result<Unit> {
        return try {
            RetrofitClient.api.deleteSupplement("Bearer $token", id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}