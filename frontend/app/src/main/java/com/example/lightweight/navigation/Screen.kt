package com.example.lightweight.navigation

sealed class Screen(val route: String)  {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Community : Screen("community")
    object MyPlans : Screen("my_plans")
    object Calendar : Screen("calendar")
    object Profile : Screen("profile")
    object CreatePlan : Screen("create_plan")
    object EditPlan : Screen("edit_plan/{planName}") {
        fun createRoute(planName: String) = "edit_plan/$planName"
    }
}
