package com.example.lightweight.data.remote

data class LoginRequest(
    val username: String,
    val password: String
)
data class LoginResponse(
    val token: String
)

data class RegisterRequest(
    val username: String,
    val password: String,
    val display_name: String
)
data class RegisterResponse(
    val id: Int,
    val username: String
)

data class WorkoutPlanResponse(
    val id: Int,
    val name: String,
    val description: String?,
    val is_published: Boolean
)

data class WorkoutPlanDetailResponse(
    val id: Int,
    val name: String,
    val description: String?,
    val is_published: Boolean,
    val exercises: List<ExerciseInPlanResponse> = emptyList()
)

data class ExerciseInPlanResponse(
    val ewp_id: Int,
    val order: Int,
    val exercise_id: Int,
    val name: String,
    val photo_url: String?,
    val sets: List<ExerciseSetResponse> = emptyList()
)

data class ExerciseSetResponse(
    val id: Int,
    val set_number: Int?,
    val reps: Int?,
    val weight: Double?,
    val machine_settings: String?,
    val is_drop_set: Boolean?
)

data class CreateWorkoutPlanRequest(
    val name: String,
    val description: String,
    val is_published: Boolean = false
)