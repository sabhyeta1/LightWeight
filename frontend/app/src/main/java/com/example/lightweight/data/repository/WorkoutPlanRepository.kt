package com.example.lightweight.data.repository

import com.example.lightweight.data.remote.CreateWorkoutPlanRequest
import com.example.lightweight.data.remote.RetrofitClient
import com.example.lightweight.data.remote.WorkoutPlanResponse
import com.example.lightweight.data.remote.WorkoutPlanDetailResponse
import com.example.lightweight.data.remote.AddExerciseToPlanRequest
import com.example.lightweight.data.remote.AddExerciseToPlanResponse
import com.example.lightweight.data.remote.ExerciseSetInput
import com.example.lightweight.data.remote.ExerciseSetResponse
import com.example.lightweight.data.remote.UpdateExerciseSetsRequest

class WorkoutPlanRepository {

    suspend fun getWorkoutPlans(token: String): Result<List<WorkoutPlanResponse>> {
        return try {
            val plans = RetrofitClient.api.getWorkoutPlans("Bearer $token")
            Result.success(plans)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

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

    suspend fun getWorkoutPlanDetails(token: String, id: Int): Result<WorkoutPlanDetailResponse> {
        return try {
            val plan = RetrofitClient.api.getWorkoutPlanDetails("Bearer $token", id)
            Result.success(plan)
        } catch (e: Exception) {
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

    // FR-07: Exercise zu Plan hinzufügen, gibt ewp_id zurück (gebraucht für FR-08)
    suspend fun addExerciseToPlan(token: String, planId: Int, exerciseId: Int, order: Int): Result<AddExerciseToPlanResponse> {
        return try {
            val result = RetrofitClient.api.addExerciseToPlan(
                "Bearer $token",
                planId,
                AddExerciseToPlanRequest(exerciseId, order)
            )
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // FR-08: Sets für eine Exercise-in-Plan-Zuordnung setzen
    suspend fun updateExerciseSets(token: String, planId: Int, ewpId: Int, sets: List<ExerciseSetInput>): Result<List<ExerciseSetResponse>> {
        return try {
            val result = RetrofitClient.api.updateExerciseSets(
                "Bearer $token",
                planId,
                ewpId,
                UpdateExerciseSetsRequest(sets)
            )
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun removeExerciseFromPlan(token: String, planId: Int, ewpId: Int): Result<Unit> {
        return try {
            RetrofitClient.api.removeExerciseFromPlan("Bearer $token", planId, ewpId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}