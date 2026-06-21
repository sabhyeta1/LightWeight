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

    // FR-07: Exercise zu Plan hinzufügen
    @POST("api/workout-plan/{id}/exercise")
    suspend fun addExerciseToPlan(
        @Header("Authorization") token: String,
        @Path("id") planId: Int,
        @Body body: AddExerciseToPlanRequest
    ): AddExerciseToPlanResponse

    // FR-08: Sets für eine Exercise-in-Plan-Zuordnung setzen (ersetzt alle bestehenden Sets)
    @PUT("api/workout-plan/{id}/exercise/{ewpId}/sets")
    suspend fun updateExerciseSets(
        @Header("Authorization") token: String,
        @Path("id") planId: Int,
        @Path("ewpId") ewpId: Int,
        @Body body: UpdateExerciseSetsRequest
    ): List<ExerciseSetResponse>

    // Exercise wieder aus einem Plan entfernen (brauchen wir beim Bearbeiten, um alte Zuordnungen zu ersetzen)
    @DELETE("api/workout-plan/{id}/exercise/{ewpId}")
    suspend fun removeExerciseFromPlan(
        @Header("Authorization") token: String,
        @Path("id") planId: Int,
        @Path("ewpId") ewpId: Int
    )

    //community
    @GET("api/community/workout-plans")
    suspend fun getCommunityPlans(
        @Header("Authorization") token: String,
        @Query("search") search: String = "",
        @Query("filterType") filterType: String = "name"
    ): List<CommunityPlanResponse>

    @POST("api/community/workout-plans/{id}/copy")
    suspend fun copyPlan(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): WorkoutPlanResponse

    @GET("api/community/workout-plans/{id}")
    suspend fun getCommunityPlanDetails(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): WorkoutPlanDetailResponse

    // FR-18
    @GET("api/community/saved")
    suspend fun getSavedPlans(
        @Header("Authorization") token: String
    ): List<CommunityPlanResponse>

    @POST("api/community/workout-plans/{id}/save")
    suspend fun savePlan(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    )

    @DELETE("api/community/workout-plans/{id}/save")
    suspend fun unsavePlan(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    )

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

    @PATCH("api/calendar/sessions/{id}")
    suspend fun updateCalendarSession(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body body: UpdateSessionRequest
    ): CalendarSessionResponse

    @PATCH("api/calendar/recurrences/{id}/sessions")
    suspend fun updateFutureSessions(
        @Header("Authorization") token: String,
        @Path("id") recurrenceId: Int,
        @Body body: UpdateFutureSessionsRequest
    )

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

    // FR-17: Profile
    @GET("api/user/profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): UserProfileResponse

    @PATCH("api/user/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body body: UpdateProfileRequest
    ): UserProfileResponse

    // Water Tracking
    @GET("api/water/status")
    suspend fun getWaterStatus(
        @Header("Authorization") token: String
    ): WaterStatusResponse

    @PUT("api/water/goal")
    suspend fun setWaterGoal(
        @Header("Authorization") token: String,
        @Body body: SetWaterGoalRequest
    ): WaterGoalResponse

    @POST("api/water/intake")
    suspend fun addWaterIntake(
        @Header("Authorization") token: String,
        @Body body: AddWaterIntakeRequest
    ): WaterLogResponse

    @DELETE("api/water/intake/{id}")
    suspend fun deleteWaterIntake(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    )

    // Supplements
    @GET("api/supplement")
    suspend fun getSupplements(
        @Header("Authorization") token: String
    ): List<SupplementResponse>

    @POST("api/supplement")
    suspend fun createSupplement(
        @Header("Authorization") token: String,
        @Body body: CreateSupplementRequest
    ): SupplementResponse

    @DELETE("api/supplement/{id}")
    suspend fun deleteSupplement(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    )
}