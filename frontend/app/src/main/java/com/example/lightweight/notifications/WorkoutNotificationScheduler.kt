package com.example.lightweight.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.lightweight.data.remote.CalendarSessionResponse
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

object WorkoutNotificationScheduler {

    private const val TAG = "WorkoutNotifScheduler"

    fun rescheduleAll(
        context: Context,
        sessions: List<CalendarSessionResponse>,
        enabled: Boolean
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        sessions.forEach { cancelAlarm(context, alarmManager, it.id) }

        if (!enabled) return

        val now = LocalDateTime.now()

        sessions.forEach { session ->
            try {
                val date = LocalDate.parse(session.session_date.take(10))
                val timePart = session.session_time.take(5)
                val time = LocalTime.parse(timePart)
                val sessionDateTime = LocalDateTime.of(date, time)
                //for testing use the minutes and comment out the hours opt
                val reminderDateTime = sessionDateTime.minusHours(1)
                // val reminderDateTime = sessionDateTime.minusMinutes(1)


                if (reminderDateTime.isAfter(now)) {
                    val triggerAtMillis = reminderDateTime
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()

                    val pendingIntent = buildPendingIntent(context, session)

                    val canExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                            alarmManager.canScheduleExactAlarms()

                    if (canExact) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            triggerAtMillis,
                            pendingIntent
                        )
                    } else {
                        alarmManager.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            triggerAtMillis,
                            pendingIntent
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error scheduling session ${session.id}: ${e.message}")
            }
        }
    }

    private fun cancelAlarm(context: Context, alarmManager: AlarmManager, sessionId: Int) {
        val intent = Intent(context, WorkoutReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            sessionId,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        ) ?: return
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    private fun buildPendingIntent(
        context: Context,
        session: CalendarSessionResponse
    ): PendingIntent {
        val intent = Intent(context, WorkoutReminderReceiver::class.java).apply {
            putExtra(WorkoutReminderReceiver.EXTRA_SESSION_ID,   session.id)
            putExtra(WorkoutReminderReceiver.EXTRA_PLAN_NAME,    session.workout_plan_name)
            putExtra(WorkoutReminderReceiver.EXTRA_SESSION_TIME, session.session_time)
        }
        return PendingIntent.getBroadcast(
            context,
            session.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}