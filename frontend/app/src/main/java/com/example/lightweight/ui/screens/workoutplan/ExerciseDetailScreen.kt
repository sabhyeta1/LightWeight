package com.example.lightweight.ui.screens.workoutplan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.Alignment
import androidx.compose.foundation.verticalScroll

data class DropSetInput(val reps: String = "", val weight: String = "")

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
    var machineSettings by remember { mutableStateOf("") }
    val dropSets = remember { mutableStateListOf<DropSetInput>() }

    val libraryState by calendarViewModel.libraryState.collectAsState()

    // Make sure the exercise library is loaded
    LaunchedEffect(Unit) {
        if (libraryState.exercises.isEmpty()) {
            calendarViewModel.loadExerciseLibrary()
        }
    }

    val exerciseId = remember(exerciseName, libraryState.exercises) {
        libraryState.exercises.firstOrNull { it.name == exerciseName }?.id
    }

    // Pre-fill fields if sets were already configured for this exercise
    LaunchedEffect(exerciseId) {
        if (exerciseId != null) {
            val existingSets = viewModel.getSetsForExercise(exerciseId)
            val mainSets = existingSets.filter { !it.is_drop_set }
            val existingDropSets = existingSets.filter { it.is_drop_set }
            if (mainSets.isNotEmpty()) {
                sets = mainSets.size.toString()
                reps = mainSets.first().reps?.toString() ?: ""
                weight = mainSets.first().weight?.toString() ?: ""
                machineSettings = mainSets.first().machine_settings ?: ""
            }
            dropSets.clear()
            existingDropSets.forEach { d ->
                dropSets.add(DropSetInput(reps = d.reps?.toString() ?: "", weight = d.weight?.toString() ?: ""))
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
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
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

                HorizontalDivider(color = SurfaceVariant)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Drop Sets",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    TextButton(onClick = { dropSets.add(DropSetInput()) }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = Blue,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Drop Set", color = Blue)
                    }
                }

                dropSets.forEachIndexed { index, dropSet ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = dropSet.reps,
                            onValueChange = { dropSets[index] = dropSet.copy(reps = it) },
                            label = { Text("Reps") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = fieldColors
                        )
                        OutlinedTextField(
                            value = dropSet.weight,
                            onValueChange = { dropSets[index] = dropSet.copy(weight = it) },
                            label = { Text("Weight (kg)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = fieldColors
                        )
                        IconButton(onClick = { dropSets.removeAt(index) }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove drop set",
                                tint = Color.Gray
                            )
                        }
                    }
                }
            }

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
                            val mainSetInputs = (1..setsCount).map { setNumber ->
                                ExerciseSetInput(
                                    set_number = setNumber,
                                    reps = reps.toIntOrNull(),
                                    weight = weight.toDoubleOrNull(),
                                    machine_settings = machineSettings.ifBlank { null },
                                    is_drop_set = false
                                )
                            }
                            val dropSetInputs = dropSets.mapIndexed { index, dropSet ->
                                ExerciseSetInput(
                                    set_number = setsCount + index + 1,
                                    reps = dropSet.reps.toIntOrNull(),
                                    weight = dropSet.weight.toDoubleOrNull(),
                                    machine_settings = null,
                                    is_drop_set = true
                                )
                            }
                            viewModel.updateSetsForExercise(exerciseId, mainSetInputs + dropSetInputs)
                            onSave()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Save", color = Color.White)
                }
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