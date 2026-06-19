package com.example.lightweight.data.repository

import com.example.lightweight.data.remote.CommunityPlanResponse
import com.example.lightweight.data.remote.RetrofitClient
import com.example.lightweight.data.remote.WorkoutPlanResponse
import com.example.lightweight.data.remote.WorkoutPlanDetailResponse

class CommunityRepository {

    suspend fun getCommunityPlans(
        token: String,
        search: String = "",
        filterType: String = "name"
    ): Result<List<CommunityPlanResponse>> {
        return try {
            val plans = RetrofitClient.api.getCommunityPlans("Bearer $token", search, filterType)
            Result.success(plans)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun copyPlan(token: String, planId: Int): Result<WorkoutPlanResponse> {
        return try {
            val plan = RetrofitClient.api.copyPlan("Bearer $token", planId)
            Result.success(plan)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCommunityPlanDetails(token: String, planId: Int): Result<WorkoutPlanDetailResponse> {
        return try {
            val plan = RetrofitClient.api.getCommunityPlanDetails("Bearer $token", planId)
            Result.success(plan)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}