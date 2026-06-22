package com.example.lightweight.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Community : Screen("community")
    object MyPlans : Screen("my_plans")
    object Calendar : Screen("calendar")
    object Profile : Screen("profile")
    object ExerciseLibrary : Screen("exercise_library")
    object CreatePlan : Screen("create_plan")
    object WorkoutPlanDetail : Screen("workout_plan_detail/{planId}/{planName}") {
        fun createRoute(planId: Int, planName: String) = "workout_plan_detail/$planId/$planName"
    }
    object EditPlan : Screen("edit_plan/{planId}/{planName}") {
        fun createRoute(planId: Int, planName: String) = "edit_plan/$planId/$planName"
    }
    object ExerciseDetail : Screen("exercise_detail/{exerciseName}") {
        fun createRoute(exerciseName: String) = "exercise_detail/${java.net.URLEncoder.encode(exerciseName, "UTF-8")}"
    }

    object CommunityPlanDetail : Screen("community_plan_detail/{planId}") {
        fun createRoute(planId: Int) = "community_plan_detail/$planId"
    }

    object WaterTracking : Screen("water_tracking")
    object Supplements : Screen("supplements")
    object SavedPlans : Screen("saved_plans")
}