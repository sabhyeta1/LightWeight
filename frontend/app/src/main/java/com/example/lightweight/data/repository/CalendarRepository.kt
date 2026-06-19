package com.example.lightweight.data.repository

import com.example.lightweight.data.remote.CalendarSessionResponse
import com.example.lightweight.data.remote.CreateRecurrenceApiResponse
import com.example.lightweight.data.remote.CreateRecurrenceRequest
import com.example.lightweight.data.remote.CreateSessionRequest
import com.example.lightweight.data.remote.UpdateSessionRequest
import com.example.lightweight.data.remote.UpdateFutureSessionsRequest
import com.example.lightweight.data.remote.ExerciseLibraryResponse
import com.example.lightweight.data.remote.MuscleGroupResponse
import com.example.lightweight.data.remote.RetrofitClient

class CalendarRepository {

    suspend fun getSessions(token: String, from: String, to: String): Result<List<CalendarSessionResponse>> {
        return try {
            val sessions = RetrofitClient.api.getCalendarSessions("Bearer $token", from, to)
            Result.success(sessions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createSession(token: String, request: CreateSessionRequest): Result<CalendarSessionResponse> {
        return try {
            val session = RetrofitClient.api.createCalendarSession("Bearer $token", request)
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateSession(token: String, sessionId: Int, request: UpdateSessionRequest): Result<CalendarSessionResponse> {
        return try {
            val session = RetrofitClient.api.updateCalendarSession("Bearer $token", sessionId, request)
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateFutureSessions(token: String, recurrenceId: Int, request: UpdateFutureSessionsRequest): Result<Unit> {
        return try {
            RetrofitClient.api.updateFutureSessions("Bearer $token", recurrenceId, request)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteSession(token: String, sessionId: Int): Result<Unit> {
        return try {
            RetrofitClient.api.deleteCalendarSession("Bearer $token", sessionId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createRecurrence(token: String, request: CreateRecurrenceRequest): Result<CreateRecurrenceApiResponse> {
        return try {
            val result = RetrofitClient.api.createRecurrence("Bearer $token", request)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteRecurrence(token: String, recurrenceId: Int): Result<Unit> {
        return try {
            RetrofitClient.api.deleteRecurrence("Bearer $token", recurrenceId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getExercises(token: String, muscleGroupId: Int? = null): Result<List<ExerciseLibraryResponse>> {
        return try {
            val exercises = RetrofitClient.api.getExercises("Bearer $token", muscleGroupId)
            Result.success(exercises)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMuscleGroups(token: String): Result<List<MuscleGroupResponse>> {
        return try {
            val groups = RetrofitClient.api.getMuscleGroups("Bearer $token")
            Result.success(groups)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}