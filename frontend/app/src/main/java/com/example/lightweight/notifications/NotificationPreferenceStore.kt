package com.example.lightweight.notifications

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.notifDataStore by preferencesDataStore(name = "notifications")

class NotificationPreferenceStore(private val context: Context) {

    companion object {
        private val NOTIFICATIONS_ENABLED_KEY = booleanPreferencesKey("notifications_enabled")
    }

    val notificationsEnabled: Flow<Boolean> = context.notifDataStore.data.map { prefs ->
        prefs[NOTIFICATIONS_ENABLED_KEY] ?: true // enabled by default
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.notifDataStore.edit { prefs ->
            prefs[NOTIFICATIONS_ENABLED_KEY] = enabled
        }
    }
}