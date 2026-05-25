package com.example.lightweight.data.remote

import retrofit2.http.*

interface ApiService {

    // Auth
    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequest): RegisterResponse

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    // Workout Plans
    @GET("api/workout-plan")
    suspend fun getWorkoutPlans(@Header("Authorization") token: String): List<WorkoutPlanResponse>

    @POST("api/workout-plan")
    suspend fun createWorkoutPlan(
        @Header("Authorization") token: String,
        @Body body: CreateWorkoutPlanRequest
    ): WorkoutPlanResponse

    @PUT("api/workout-plan/{id}")
    suspend fun updateWorkoutPlan(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body body: CreateWorkoutPlanRequest
    ): WorkoutPlanResponse

    @DELETE("api/workout-plan/{id}")
    suspend fun deleteWorkoutPlan(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    )
}