package com.example.lightweight.data.repository

import com.example.lightweight.data.remote.AddWaterIntakeRequest
import com.example.lightweight.data.remote.RetrofitClient
import com.example.lightweight.data.remote.SetWaterGoalRequest
import com.example.lightweight.data.remote.WaterGoalResponse
import com.example.lightweight.data.remote.WaterLogResponse
import com.example.lightweight.data.remote.WaterStatusResponse

class WaterRepository {

    suspend fun getStatus(token: String): Result<WaterStatusResponse> {
        return try {
            val status = RetrofitClient.api.getWaterStatus("Bearer $token")
            Result.success(status)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setGoal(token: String, targetMl: Int): Result<WaterGoalResponse> {
        return try {
            val goal = RetrofitClient.api.setWaterGoal("Bearer $token", SetWaterGoalRequest(targetMl))
            Result.success(goal)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addIntake(token: String, amountMl: Int? = null): Result<WaterLogResponse> {
        return try {
            val log = RetrofitClient.api.addWaterIntake("Bearer $token", AddWaterIntakeRequest(amountMl))
            Result.success(log)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteIntake(token: String, id: Int): Result<Unit> {
        return try {
            RetrofitClient.api.deleteWaterIntake("Bearer $token", id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}