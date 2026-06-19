package com.example.lightweight.ui.screens.workoutplan

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lightweight.ui.components.LightWeightBottomBar
import com.example.lightweight.ui.components.LightWeightHeader
import com.example.lightweight.ui.theme.Background
import com.example.lightweight.ui.theme.LightWeightTheme
import com.example.lightweight.ui.viewmodel.WorkoutPlanViewModel
import com.example.lightweight.ui.viewmodel.CalendarViewModel
import kotlinx.coroutines.launch

@Composable
fun EditWorkoutPlanScreen(
    planId: Int = 0,
    planName: String = "",
    onCancel: () -> Unit = {},
    onSaveSuccess: () -> Unit = {},
    onEditExercise: (String) -> Unit = {},
    viewModel: WorkoutPlanViewModel = viewModel(),
    calendarViewModel: CalendarViewModel = viewModel()
) {
//    val snackbarHostState = remember { SnackbarHostState() }
//    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    val plan = uiState.plans.firstOrNull { it.id == planId }

    LaunchedEffect(planId) {
        viewModel.loadPlanIntoDraft(planId)
    }

    Scaffold(
        topBar = { LightWeightHeader() },
        bottomBar = { LightWeightBottomBar(currentScreen = "My Plans") },
        containerColor = Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column {
                Text(
                    text = "Edit",
                    color = Color.Gray,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = planName,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            WorkoutPlanForm(
                modifier = Modifier.weight(1f),
                initialName = plan?.name ?: planName,
                initialDescription = plan?.description ?: "",
                initialIsPublic = plan?.is_published ?: false,
                onCancel = { viewModel.clearDraftPlan(); onCancel() },
                onEditExercise = onEditExercise,
                viewModel = viewModel,
                calendarViewModel = calendarViewModel,
                onSave = { name, description, _, isPublic ->
                    viewModel.updatePlan(planId, name, description, isPublic)
                    onSaveSuccess()
                }
//                initialName = planName,
//                //initialDescription = planDescription,
//                //initialIsPublic = isPublic,
//                //initialSelectedExercises = selectedExercises,
//                onCancel = onCancel,
//                onEditExercise = onEditExercise,
//                onSave = { name, description, _, isPublic ->
//                    viewModel.updatePlan(planId, name, description, isPublic)
//                    onSaveSuccess()
//                }
//                onSave = { _, _, _, _ ->
//                    scope.launch {
//                        snackbarHostState.showSnackbar("Saved changes")
//                        onSaveSuccess()
//                    }
//                }
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
fun EditWorkoutPlanScreenPreview() {
    LightWeightTheme {
        EditWorkoutPlanScreen()
    }
}