package com.example.lightweight.data.repository

import com.example.lightweight.data.remote.CreateWorkoutPlanRequest
import com.example.lightweight.data.remote.RetrofitClient
import com.example.lightweight.data.remote.WorkoutPlanResponse

class WorkoutPlanRepository {

    suspend fun getWorkoutPlans(token: String): Result<List<WorkoutPlanResponse>> {
        return try {
            val plans = RetrofitClient.api.getWorkoutPlans("Bearer $token")
            Result.success(plans)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

//    suspend fun createWorkoutPlan(token: String, name: String, description: String, isPublic: Boolean): Result<WorkoutPlanResponse> {
//        return try {
//            val plan = RetrofitClient.api.createWorkoutPlan(
//                "Bearer $token",
//                CreateWorkoutPlanRequest(name, description, isPublic)
//            )
//            Result.success(plan)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
suspend fun createWorkoutPlan(token: String, name: String, description: String, isPublic: Boolean): Result<WorkoutPlanResponse> {
    return try {
        android.util.Log.d("WorkoutPlanRepo", "createWorkoutPlan called, token: $token, name: $name")
        val plan = RetrofitClient.api.createWorkoutPlan(
            "Bearer $token",
            CreateWorkoutPlanRequest(name, description, isPublic)
        )
        Result.success(plan)
    } catch (e: Exception) {
        android.util.Log.e("WorkoutPlanRepo", "Error: ${e.message}")
        Result.failure(e)
    }
}

    suspend fun deleteWorkoutPlan(token: String, id: Int): Result<Unit> {
        return try {
            RetrofitClient.api.deleteWorkoutPlan("Bearer $token", id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateWorkoutPlan(token: String, id: Int, name: String, description: String, isPublic: Boolean): Result<WorkoutPlanResponse> {
        return try {
            val plan = RetrofitClient.api.updateWorkoutPlan(
                "Bearer $token",
                id,
                CreateWorkoutPlanRequest(name, description, isPublic)
            )
            Result.success(plan)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}