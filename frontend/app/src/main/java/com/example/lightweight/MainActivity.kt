package com.example.lightweight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.lightweight.ui.screens.auth.LoginScreen
import com.example.lightweight.ui.screens.auth.RegisterScreen
import com.example.lightweight.ui.theme.LightWeightTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LightWeightTheme {
                var currentScreen by remember { mutableStateOf("login") }

                when (currentScreen) {
                    "login" -> LoginScreen(
                        onNavigateToRegister = { currentScreen = "register" },
                        onLoginSuccess = { /* TODO: navigate to home */ }
                    )
                    "register" -> RegisterScreen(
                        onNavigateToLogin = { currentScreen = "login" },
                        onRegisterSuccess = { currentScreen = "login" }
                    )
                }
            }
        }
    }
}