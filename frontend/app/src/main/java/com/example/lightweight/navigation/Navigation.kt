package com.example.lightweight.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.lightweight.data.local.TokenStore
import com.example.lightweight.ui.screens.auth.LoginScreen
import com.example.lightweight.ui.screens.auth.RegisterScreen
import com.example.lightweight.ui.screens.home.HomeScreen
import com.example.lightweight.ui.screens.calendar.CalendarScreen
import com.example.lightweight.ui.screens.community.CommunityScreen
import com.example.lightweight.ui.screens.exerciselibrary.ExerciseLibraryScreen
import com.example.lightweight.ui.screens.workoutplan.MyPlansScreen
import com.example.lightweight.ui.screens.workoutplan.CreateWorkoutPlanScreen
import com.example.lightweight.ui.screens.workoutplan.EditWorkoutPlanScreen
import com.example.lightweight.ui.screens.workoutplan.ExerciseDetailScreen
import com.example.lightweight.ui.screens.workoutplan.WorkoutPlanDetailScreen
import com.example.lightweight.ui.screens.profile.ProfileScreen
import com.example.lightweight.ui.viewmodel.AuthViewModel
import com.example.lightweight.ui.viewmodel.CalendarViewModel
import com.example.lightweight.ui.viewmodel.WorkoutPlanViewModel
import com.example.lightweight.ui.viewmodel.CommunityViewModel
import com.example.lightweight.ui.viewmodel.WorkoutPlanUiState
import com.example.lightweight.ui.viewmodel.ProfileViewModel
import kotlinx.coroutines.flow.first
import com.example.lightweight.ui.screens.profile.WaterTrackingScreen
import com.example.lightweight.ui.screens.profile.SupplementsScreen
import com.example.lightweight.ui.screens.community.SavedPlansScreen

@Composable
fun Navigation(navController: NavHostController) {
    val workoutPlanViewModel: WorkoutPlanViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    // CalendarViewModel is shared between CalendarScreen and ExerciseLibraryScreen
    val calendarViewModel: CalendarViewModel = viewModel()

    val context = LocalContext.current
    val tokenStore = remember { TokenStore(context) }

    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val savedToken = tokenStore.getToken().first()

        startDestination = if (savedToken.isNullOrBlank()) {
            Screen.Login.route
        } else {
            Screen.Home.route
        }
    }

    if (startDestination == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }


    NavHost(
        navController = navController,
        startDestination = startDestination!!
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
                onRegisterSuccess = {
                    workoutPlanViewModel.loadPlans()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateTo = { route -> navController.navigate(route) }
            )
        }

        composable(Screen.Community.route) {
            CommunityScreen(
                onNavigateTo = { route -> navController.navigate(route) },
                onViewPlan = { planId ->
                    navController.navigate(Screen.CommunityPlanDetail.createRoute(planId))
                }
            )
        }

        composable(
            route = Screen.CommunityPlanDetail.route,
            arguments = listOf(navArgument("planId") { type = NavType.IntType })
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getInt("planId") ?: 0
            val communityViewModel: CommunityViewModel = viewModel()
            val uiState by communityViewModel.uiState.collectAsState()

            LaunchedEffect(planId) {
                communityViewModel.loadPlanDetails(planId)
            }

            WorkoutPlanDetailScreen(
                plan = uiState.selectedPlanDetails,
                isLoading = uiState.isLoadingDetails,
                errorMessage = uiState.errorMessage,
                onNavigateTo = { route -> navController.navigate(route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.MyPlans.route) {
            MyPlansScreen(
                viewModel = workoutPlanViewModel,
                onNavigateToCreate = { navController.navigate(Screen.CreatePlan.route) },
                onNavigateToSaved = { navController.navigate(Screen.SavedPlans.route) },
                onViewPlan = { planId, planName ->
                    navController.navigate(Screen.WorkoutPlanDetail.createRoute(planId, planName))
                },
                onEditPlan = { planId, planName ->
                    navController.navigate(Screen.EditPlan.createRoute(planId, planName))
                },
                onDeletePlan = {},
                onNavigateTo = { route -> navController.navigate(route) }
            )
        }

        composable(Screen.SavedPlans.route) {
            SavedPlansScreen(
                onNavigateTo = { route -> navController.navigate(route) },
                onViewPlan = { planId ->
                    navController.navigate(Screen.CommunityPlanDetail.createRoute(planId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.WorkoutPlanDetail.route,
            arguments = listOf(
                navArgument("planId") { type = NavType.IntType },
                navArgument("planName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getInt("planId") ?: 0
            val uiState by workoutPlanViewModel.uiState.collectAsState()

            LaunchedEffect(planId) {
                workoutPlanViewModel.loadPlanDetails(planId)
            }

            WorkoutPlanDetailScreen(
                plan = uiState.currentPlanDetails,
                isLoading = uiState.isLoading,
                errorMessage = uiState.errorMessage,
                onNavigateTo = { route: String -> navController.navigate(route) },
                onBack = { navController.popBackStack() }
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
                calendarViewModel = calendarViewModel,
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
                calendarViewModel = calendarViewModel,
                onSave = { navController.popBackStack() },
                onCancel = { navController.popBackStack() },
                onEditExercise = { exerciseName ->
                    navController.navigate(Screen.ExerciseDetail.createRoute(exerciseName))
                }
            )
        }

        composable(
            route = Screen.ExerciseDetail.route,
            arguments = listOf(navArgument("exerciseName") { type = NavType.StringType })
        ) { backStackEntry ->
            val exerciseName = java.net.URLDecoder.decode(backStackEntry.arguments?.getString("exerciseName") ?: "", "UTF-8")
            ExerciseDetailScreen(
                exerciseName = exerciseName,
                onSave = { navController.popBackStack() },
                onCancel = { navController.popBackStack() },
                viewModel = workoutPlanViewModel,
                calendarViewModel = calendarViewModel
            )
        }

        // FR-12, FR-13, FR-14, FR-15 — Calendar
        composable(Screen.Calendar.route) {
            CalendarScreen(
                onNavigateTo = { route -> navController.navigate(route) },
                calendarViewModel = calendarViewModel,
                planViewModel = workoutPlanViewModel
            )
        }

        // FR-16 — Exercise Library
        composable(Screen.ExerciseLibrary.route) {
            ExerciseLibraryScreen(
                onNavigateTo = { route -> navController.navigate(route) },
                viewModel = calendarViewModel
            )
        }

        composable(Screen.Profile.route) {
            val profileViewModel: ProfileViewModel = viewModel()
            ProfileScreen(
                onNavigateTo = { route -> navController.navigate(route) },
                onNavigateToWater = { navController.navigate(Screen.WaterTracking.route) },
                onNavigateToSupplements = { navController.navigate(Screen.Supplements.route) },
                onLogout = {
                    authViewModel.logout()
                    workoutPlanViewModel.clearPlans()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                viewModel = profileViewModel
            )
        }

        composable(Screen.WaterTracking.route) {
            WaterTrackingScreen(
                onNavigateTo = { route -> navController.navigate(route) },
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Supplements.route) {
            SupplementsScreen(
                onNavigateTo = { route -> navController.navigate(route) },
                onBack = { navController.popBackStack() }
            )
        }
    }
}