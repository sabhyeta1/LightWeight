package com.example.lightweight.ui.screens.workoutplan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lightweight.ui.components.LightWeightHeader
import com.example.lightweight.ui.components.LightWeightBottomBar
import com.example.lightweight.ui.theme.Background
import com.example.lightweight.ui.theme.Blue
import com.example.lightweight.ui.theme.Surface
import com.example.lightweight.ui.theme.SurfaceVariant
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lightweight.ui.theme.LightWeightTheme
import com.example.lightweight.ui.viewmodel.WorkoutPlanViewModel
import com.example.lightweight.ui.viewmodel.CalendarViewModel
@Composable
fun CreateWorkoutPlanScreen(
    onSave: () -> Unit = {},
    onCancel: () -> Unit = {},
    onEditExercise: (String) -> Unit = {},
    viewModel: WorkoutPlanViewModel = viewModel(),
    calendarViewModel: CalendarViewModel = viewModel()
) {

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
            Text(
                text = "New Plan",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            WorkoutPlanForm(
                onSave = { name, description, exercises, isPublic ->
                    viewModel.createPlan(name, description, isPublic)
                    onSave()
                },
                onCancel = onCancel,
                onEditExercise = onEditExercise,
                viewModel = viewModel,
                calendarViewModel = calendarViewModel
            )
        }
    }
}

@Composable
fun WorkoutPlanForm(
    initialName: String = "",
    initialDescription: String = "",
    initialIsPublic: Boolean = false,
    initialSelectedExercises: List<String> = emptyList(),
    onSave: (String, String, List<String>, Boolean) -> Unit = { _, _, _, _ -> },
    onCancel: () -> Unit = {},
    onEditExercise: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: WorkoutPlanViewModel = viewModel(),
    calendarViewModel: CalendarViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }

    val draftPlanState by viewModel.draftPlan.collectAsState()
    val libraryState by calendarViewModel.libraryState.collectAsState()
    val planName = draftPlanState.name
    val planDescription = draftPlanState.description
    val isPublic = draftPlanState.isPublic
    var showPublishDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        calendarViewModel.loadExerciseLibrary()
        viewModel.initDraftIfNeeded(initialName, initialDescription, initialIsPublic)
    }

    val filteredExercises = remember(searchQuery, libraryState.exercises) {
        libraryState.exercises.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = planName,
            onValueChange = { viewModel.updateDraftName(it) },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Blue,
                unfocusedBorderColor = SurfaceVariant,
                focusedLabelColor = Blue,
                unfocusedLabelColor = Color.Gray,
                cursorColor = Blue,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        OutlinedTextField(
            value = planDescription,
            onValueChange = { viewModel.updateDraftDescription(it) },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Blue,
                unfocusedBorderColor = SurfaceVariant,
                focusedLabelColor = Blue,
                unfocusedLabelColor = Color.Gray,
                cursorColor = Blue,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Text(
            text = "Select Exercises",
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Surface, RoundedCornerShape(12.dp))
                .border(1.dp, SurfaceVariant, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search exercises...", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Blue,
                    unfocusedBorderColor = SurfaceVariant,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(filteredExercises) { exercise ->
                    val isSelected = draftPlanState.selectedExercises.any { it.exerciseId == exercise.id }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = {
                                    viewModel.toggleExerciseSelection(exercise.id, exercise.name)
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Blue,
                                    uncheckedColor = SurfaceVariant,
                                    checkmarkColor = Color.White
                                )
                            )
                            Text(
                                text = exercise.name,
                                color = if (isSelected) Color.White else Color.Gray,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        IconButton(
                            onClick = { onEditExercise(exercise.name) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit ${exercise.name}",
                                tint = if (isSelected) Blue else Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        if (showPublishDialog) {
            AlertDialog(
                onDismissRequest = {
                    showPublishDialog = false
                    viewModel.updateDraftIsPublic(false)
                },
                title = { Text("Publish Plan?") },
                text = { Text("This plan will be visible to all LightWeight users. Your name will appear as the creator.") },
                confirmButton = {
                    TextButton(onClick = {
                        showPublishDialog = false
                        viewModel.updateDraftIsPublic(true)
                    }) {
                        Text("Confirm", color = Blue)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showPublishDialog = false
                        viewModel.updateDraftIsPublic(false)
                    }) {
                        Text("Cancel", color = Color.Gray)
                    }
                },
                containerColor = Surface,
                titleContentColor = Color.White,
                textContentColor = Color.LightGray
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Visible to public?", color = Color.White, fontSize = 16.sp)
            Switch(
                checked = isPublic,
                onCheckedChange = { newValue ->
                    if (newValue) {
                        showPublishDialog = true
                    } else {
                        viewModel.updateDraftIsPublic(false)
                    }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Blue,
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = SurfaceVariant
                )
            )
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
                onClick = { onSave(planName, planDescription, draftPlanState.selectedExercises.map { it.name }, isPublic) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Blue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
fun CreateWorkoutPlanScreenPreview() {
    LightWeightTheme {
        CreateWorkoutPlanScreen()
    }
}
