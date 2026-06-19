package com.example.lightweight.ui.screens.workoutplan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
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
                modifier = Modifier.weight(1f),
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

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.fillMaxSize().verticalScroll(scrollState),
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
            maxLines = 4,
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
                .fillMaxWidth()
                .height(300.dp)
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

            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                items(filteredExercises) { exercise ->
                    val isSelected = draftPlanState.selectedExercises.any { it.exerciseId == exercise.id }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
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

                            val imageUrl = exercise.photo_url?.let { "http://10.0.2.2:3000$it" }
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = exercise.name,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                contentScale = ContentScale.Crop,
                                error = painterResource(id = android.R.drawable.ic_menu_gallery),
                                placeholder = painterResource(id = android.R.drawable.ic_menu_gallery)
                            )

                            Column(modifier = Modifier.padding(start = 8.dp)) {
                                Text(
                                    text = exercise.name,
                                    color = if (isSelected) Color.White else Color.Gray
                                )
                                if (!exercise.description.isNullOrBlank()) {
                                    Text(
                                        text = exercise.description,
                                        color = Color.Gray,
                                        fontSize = 12.sp,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                        IconButton(
                            onClick = { onEditExercise(exercise.name) },
                            enabled = isSelected,
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
            /*LazyColumn(modifier = Modifier.fillMaxWidth()) {
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
                            enabled = isSelected,
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
            }*/
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

        if (planName.isBlank() || draftPlanState.selectedExercises.isEmpty()) {
            Text(
                text = "Enter a name and select at least one exercise to save!",
                color = Color.Gray,
                fontSize = 13.sp
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
                onClick = {
                    if (planName.isNotBlank() && draftPlanState.selectedExercises.isNotEmpty()) {
                        onSave(planName, planDescription, draftPlanState.selectedExercises.map { it.name }, isPublic)
                    }
                },
                enabled = planName.isNotBlank() && draftPlanState.selectedExercises.isNotEmpty(),
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
