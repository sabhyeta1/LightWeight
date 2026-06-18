package com.example.lightweight.ui.screens.workoutplan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lightweight.ui.components.LightWeightBottomBar
import com.example.lightweight.ui.components.LightWeightHeader
import com.example.lightweight.ui.theme.Background
import com.example.lightweight.ui.theme.Blue
import com.example.lightweight.ui.theme.LightWeightTheme
import com.example.lightweight.ui.theme.SurfaceVariant
import com.example.lightweight.ui.viewmodel.CalendarViewModel
import com.example.lightweight.ui.viewmodel.WorkoutPlanViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lightweight.data.remote.ExerciseSetInput

@Composable
fun ExerciseDetailScreen(
    exerciseName: String = "Bench Press",
    onSave: () -> Unit = {},
    onCancel: () -> Unit = {},
    viewModel: WorkoutPlanViewModel = viewModel(),
    calendarViewModel: CalendarViewModel = viewModel()
) {
    var sets by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var machineSettings by remember { mutableStateOf("")}

    // Exercise-Library wurde schon von CreateWorkoutPlanScreen geladen, hier nur die id zum Namen suchen
    val libraryState by calendarViewModel.libraryState.collectAsState()
    val exerciseId = remember(exerciseName, libraryState.exercises) {
        libraryState.exercises.firstOrNull { it.name == exerciseName }?.id
    }

    // Falls für diese Exercise schon mal Sets eingegeben wurden, Felder damit vorbefüllen
    LaunchedEffect(exerciseId) {
        if (exerciseId != null) {
            val existingSets = viewModel.getSetsForExercise(exerciseId)
            if (existingSets.isNotEmpty()) {
                sets = existingSets.size.toString()
                reps = existingSets.first().reps?.toString() ?: ""
                weight = existingSets.first().weight?.toString() ?: ""
                machineSettings = existingSets.first().machine_settings ?: ""
            }
        }
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
                    text = "Configure",
                    color = Color.Gray,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = exerciseName,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            val fieldColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Blue,
                unfocusedBorderColor = SurfaceVariant,
                focusedLabelColor = Blue,
                unfocusedLabelColor = Color.Gray,
                cursorColor = Blue,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )

            OutlinedTextField(
                value = sets,
                onValueChange = { sets = it },
                label = { Text("Sets") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = fieldColors
            )

            OutlinedTextField(
                value = reps,
                onValueChange = { reps = it },
                label = { Text("Reps") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = fieldColors
            )

            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Weight (kg)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = fieldColors
            )

            OutlinedTextField(
                value = machineSettings,
                onValueChange = { machineSettings = it },
                label = { Text("Machine Settings") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                colors = fieldColors
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariant),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancel", color = Color.White)
                }
                Button(
                    onClick = {
                        if (exerciseId != null) {
                            val setsCount = sets.toIntOrNull() ?: 1
                            val newSets = (1..setsCount).map { setNumber ->
                                ExerciseSetInput(
                                    set_number = setNumber,
                                    reps = reps.toIntOrNull(),
                                    weight = weight.toDoubleOrNull(),
                                    machine_settings = machineSettings.ifBlank { null }
                                )
                            }
                            viewModel.updateSetsForExercise(exerciseId, newSets)
                        }
                        onSave()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Save", color = Color.White)
                }
                /*Button(
                    onClick = onSave,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Save", color = Color.White)
                }*/
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
fun ExerciseDetailScreenPreview() {
    LightWeightTheme {
        ExerciseDetailScreen()
    }
}