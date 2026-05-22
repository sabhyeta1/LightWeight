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
import com.example.lightweight.ui.theme.LightWeightTheme

@Composable
fun CreateWorkoutPlanScreen(
    onSave: () -> Unit = {},
    onCancel: () -> Unit = {},
    onEditExercise: (String) -> Unit = {},
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
                onSave = { _, _, _, _ -> onSave() },
                onCancel = onCancel,
                onEditExercise = onEditExercise
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
    modifier: Modifier = Modifier
) {
    var planName by remember { mutableStateOf(initialName) }
    var planDescription by remember { mutableStateOf(initialDescription) }
    var searchQuery by remember { mutableStateOf("") }
    var isPublic by remember { mutableStateOf(initialIsPublic) }

    val allExercises = remember {
        listOf(
            "Bench Press", "Squat", "Deadlift", "Pull Up",
            "Overhead Press", "Barbell Row", "Leg Press", "Bicep Curl",
            "Tricep Extension", "Lateral Raise", "Lunges", "Plank"
        )
    }
    val selectedExercises = remember { mutableStateListOf<String>().apply { addAll(initialSelectedExercises) } }
    val filteredExercises = remember(searchQuery) {
        allExercises.filter { it.contains(searchQuery, ignoreCase = true) }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = planName,
            onValueChange = { planName = it },
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
            onValueChange = { planDescription = it },
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
                    val isSelected = selectedExercises.contains(exercise)
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
                                    if (it) selectedExercises.add(exercise)
                                    else selectedExercises.remove(exercise)
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Blue,
                                    uncheckedColor = SurfaceVariant,
                                    checkmarkColor = Color.White
                                )
                            )
                            Text(
                                text = exercise, 
                                color = if (isSelected) Color.White else Color.Gray,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        IconButton(
                            onClick = { onEditExercise(exercise) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit $exercise",
                                tint = if (isSelected) Blue else Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Visible to public?", color = Color.White, fontSize = 16.sp)
            Switch(
                checked = isPublic,
                onCheckedChange = { isPublic = it },
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
                onClick = { onSave(planName, planDescription, selectedExercises.toList(), isPublic) },
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