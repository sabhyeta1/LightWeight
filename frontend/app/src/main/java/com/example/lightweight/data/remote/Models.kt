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

// Calendar

data class CalendarSessionResponse(
    val id: Int,
    val user_id: Int,
    val workout_plan_id: Int,
    val workout_plan_name: String,
    val recurrence_rule_id: Int?,
    val session_date: String,   // YYYY-MM-DD
    val session_time: String,   // HH:MM:SS
    val color_id: Int,
    val status: String,
    val created_at: String
)

data class CreateSessionRequest(
    val workout_plan_id: Int,
    val session_date: String,   // YYYY-MM-DD
    val session_time: String,   // HH:MM
    val color_id: Int = 1
)

data class CreateRecurrenceRequest(
    val workout_plan_id: Int,
    val type: String,           // "weekdays" | "interval"
    val weekdays: List<Int>?,   // 0=Sun … 6=Sat, null for interval
    val interval_days: Int?,    // 1-7, null for weekdays
    val start_date: String,     // YYYY-MM-DD
    val end_date: String,       // YYYY-MM-DD
    val session_time: String,   // HH:MM
    val color_id: Int = 1
)

data class RecurrenceResponse(
    val id: Int,
    val user_id: Int,
    val workout_plan_id: Int,
    val type: String,
    val weekdays: List<Int>?,
    val interval_days: Int?,
    val start_date: String,
    val end_date: String,
    val session_time: String,
    val color_id: Int,
    val is_active: Boolean,
    val created_at: String
)

data class CreateRecurrenceApiResponse(
    val recurrence: RecurrenceResponse,
    val sessions: List<CalendarSessionResponse>
)

// Exercise Library

data class ExerciseLibraryResponse(
    val id: Int,
    val name: String,
    val description: String?,
    val photo_url: String?,
    val muscle_groups: List<String>
)

data class MuscleGroupResponse(
    val id: Int,
    val name: String
)