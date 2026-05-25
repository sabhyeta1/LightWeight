package com.example.lightweight.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.lightweight.ui.screens.auth.LoginScreen
import com.example.lightweight.ui.screens.auth.RegisterScreen
import com.example.lightweight.ui.screens.home.HomeScreen
import com.example.lightweight.ui.screens.community.CommunityScreen
import com.example.lightweight.ui.screens.workoutplan.MyPlansScreen
import com.example.lightweight.ui.screens.workoutplan.CreateWorkoutPlanScreen
import com.example.lightweight.ui.screens.workoutplan.EditWorkoutPlanScreen
import com.example.lightweight.ui.screens.workoutplan.ExerciseDetailScreen
import com.example.lightweight.ui.screens.calendar.CalendarScreen
import com.example.lightweight.ui.screens.profile.ProfileScreen
import com.example.lightweight.ui.viewmodel.AuthViewModel
import com.example.lightweight.ui.viewmodel.WorkoutPlanViewModel

@Composable
fun Navigation(navController: NavHostController) {
    val workoutPlanViewModel: WorkoutPlanViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = {
                    workoutPlanViewModel.loadPlans()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = { navController.popBackStack() }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateTo = { route -> navController.navigate(route) }
            )
        }

        composable(Screen.Community.route) {
            CommunityScreen(
                onNavigateTo = { route -> navController.navigate(route) }
            )
        }

        composable(Screen.MyPlans.route) {
            MyPlansScreen(
                viewModel = workoutPlanViewModel,
                onNavigateToCreate = { navController.navigate(Screen.CreatePlan.route) },
                onEditPlan = { planId, planName ->
                    navController.navigate(Screen.EditPlan.createRoute(planId, planName))
                },
                onDeletePlan = {},
                onNavigateTo = { route -> navController.navigate(route) }
            )
        }
        composable(
            route = Screen.EditPlan.route,
            arguments = listOf(
                navArgument("planId") { type = NavType.IntType },
                navArgument("planName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getInt("planId") ?: 0
            val planName = backStackEntry.arguments?.getString("planName") ?: ""
            EditWorkoutPlanScreen(
                viewModel = workoutPlanViewModel,
                planId = planId,
                planName = planName,
                onCancel = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() },
                onEditExercise = { exerciseName ->
                    navController.navigate(Screen.ExerciseDetail.createRoute(exerciseName))
                }
            )
        }

        composable(Screen.CreatePlan.route) {
            CreateWorkoutPlanScreen(
                viewModel = workoutPlanViewModel,
                onSave = { navController.popBackStack() },
                onCancel = { navController.popBackStack() },
                onEditExercise = { exerciseName ->
                    navController.navigate(Screen.ExerciseDetail.createRoute(exerciseName))
                }
            )
        }

//        composable(
//            route = Screen.EditPlan.route,
//            arguments = listOf(navArgument("planName") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val planName = backStackEntry.arguments?.getString("planName") ?: ""
//            EditWorkoutPlanScreen(
//                planName = planName,
//                onCancel = { navController.popBackStack() },
//                onSaveSuccess = { navController.popBackStack() },
//                onEditExercise = { exerciseName ->
//                    navController.navigate(Screen.ExerciseDetail.createRoute(exerciseName))
//                }
//            )
//        }

        composable(
            route = Screen.ExerciseDetail.route,
            arguments = listOf(navArgument("exerciseName") { type = NavType.StringType })
        ) { backStackEntry ->
            val exerciseName = backStackEntry.arguments?.getString("exerciseName") ?: ""
            ExerciseDetailScreen(
                exerciseName = exerciseName,
                onSave = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }

        composable(Screen.Calendar.route) {
            CalendarScreen(
                onNavigateTo = { route -> navController.navigate(route) }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateTo = { route -> navController.navigate(route)},
                onLogout = {
                    authViewModel.logout()
                    workoutPlanViewModel.clearPlans()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}