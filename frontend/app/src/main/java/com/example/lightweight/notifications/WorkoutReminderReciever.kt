package com.example.lightweight.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class WorkoutReminderReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_SESSION_ID    = "session_id"
        const val EXTRA_PLAN_NAME     = "plan_name"
        const val EXTRA_SESSION_TIME  = "session_time"
        const val CHANNEL_ID          = "workout_reminders"
        const val CHANNEL_NAME        = "Workout Reminders"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val planName    = intent.getStringExtra(EXTRA_PLAN_NAME)    ?: "Workout"
        val sessionTime = intent.getStringExtra(EXTRA_SESSION_TIME) ?: ""
        val sessionId   = intent.getIntExtra(EXTRA_SESSION_ID, 0)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Reminders for upcoming workout sessions"
        }
        notificationManager.createNotificationChannel(channel)

        val timeDisplay = sessionTime.take(5)

        val notification = androidx.core.app.NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Workout in 1 hour ⏱")
            .setContentText("$planName starts at $timeDisplay — time to get ready!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(sessionId, notification)
    }
}