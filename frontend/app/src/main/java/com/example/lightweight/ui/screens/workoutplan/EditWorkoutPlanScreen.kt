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
import com.example.lightweight.ui.components.LightWeightBottomBar
import com.example.lightweight.ui.components.LightWeightHeader
import com.example.lightweight.ui.theme.Background
import com.example.lightweight.ui.theme.LightWeightTheme
import kotlinx.coroutines.launch

@Composable
fun EditWorkoutPlanScreen(
    planName: String = "Bulk Phase 1",
    planDescription: String = "My current bulking routine",
    isPublic: Boolean = true,
    selectedExercises: List<String> = listOf("Bench Press", "Squat", "Deadlift"),
    onCancel: () -> Unit = {},
    onSaveSuccess: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { LightWeightHeader() },
        bottomBar = { LightWeightBottomBar(currentScreen = "My Plans") },
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                initialName = planName,
                initialDescription = planDescription,
                initialIsPublic = isPublic,
                initialSelectedExercises = selectedExercises,
                onCancel = onCancel,
                onSave = { _, _, _, _ ->
                    scope.launch {
                        snackbarHostState.showSnackbar("Saved changes")
                        onSaveSuccess()
                    }
                }
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
