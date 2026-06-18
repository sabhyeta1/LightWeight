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

    @GET("api/workout-plan/{id}")
    suspend fun getWorkoutPlanDetails(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): WorkoutPlanDetailResponse

    @DELETE("api/workout-plan/{id}")
    suspend fun deleteWorkoutPlan(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    )

    //community
    @GET("api/community")
    suspend fun getCommunityPlans(
        @Header("Authorization") token: String,
        @Query("search") search: String = "",
        @Query("filterType") filterType: String = "name"
    ): List<CommunityPlanResponse>

    @POST("api/community/{id}/copy")
    suspend fun copyPlan(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): WorkoutPlanResponse

   // Calendar

    @GET("api/calendar/sessions")
    suspend fun getCalendarSessions(
        @Header("Authorization") token: String,
        @Query("from") from: String,
        @Query("to") to: String
    ): List<CalendarSessionResponse>

    @POST("api/calendar/sessions")
    suspend fun createCalendarSession(
        @Header("Authorization") token: String,
        @Body body: CreateSessionRequest
    ): CalendarSessionResponse

    @DELETE("api/calendar/sessions/{id}")
    suspend fun deleteCalendarSession(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    )

    @POST("api/calendar/recurrences")
    suspend fun createRecurrence(
        @Header("Authorization") token: String,
        @Body body: CreateRecurrenceRequest
    ): CreateRecurrenceApiResponse

    @DELETE("api/calendar/recurrences/{id}")
    suspend fun deleteRecurrence(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    )

    // Exercises & Muscle Groups

    @GET("api/exercise")
    suspend fun getExercises(
        @Header("Authorization") token: String,
        @Query("muscleGroupId") muscleGroupId: Int? = null
    ): List<ExerciseLibraryResponse>

    @GET("api/muscle-group")
    suspend fun getMuscleGroups(
        @Header("Authorization") token: String
    ): List<MuscleGroupResponse>
}