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

data class CreateWorkoutPlanRequest(
    val name: String,
    val description: String,
    val is_published: Boolean = false
)