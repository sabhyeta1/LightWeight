package com.example.lightweight.ui.screens.exerciselibrary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lightweight.data.remote.ExerciseLibraryResponse
import com.example.lightweight.ui.components.LightWeightBottomBar
import com.example.lightweight.ui.components.LightWeightHeader
import com.example.lightweight.ui.theme.*
import com.example.lightweight.ui.viewmodel.CalendarViewModel

@Composable
fun ExerciseLibraryScreen(
    onNavigateTo: (String) -> Unit = {},
    viewModel: CalendarViewModel = viewModel()
) {
    val libraryState by viewModel.libraryState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadExerciseLibrary()
    }

    val filteredExercises = viewModel.getFilteredExercises()

    Scaffold(
        topBar = { LightWeightHeader() },
        bottomBar = {
            LightWeightBottomBar(currentScreen = "Library", onNavigateTo = onNavigateTo)
        },
        containerColor = Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Exercise Library",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )

            // Search bar
            OutlinedTextField(
                value = libraryState.searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search exercises…", color = Subtext) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Subtext) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = SurfaceVariant,
                    focusedBorderColor = Blue,
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White,
                    cursorColor = Blue,
                    unfocusedContainerColor = Surface,
                    focusedContainerColor = Surface
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Muscle group filter chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = libraryState.selectedMuscleGroupId == null,
                        onClick = { viewModel.filterByMuscleGroup(null) },
                        label = { Text("All") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Blue,
                            selectedLabelColor = Color.White,
                            containerColor = SurfaceVariant,
                            labelColor = Color.White
                        )
                    )
                }
                items(libraryState.muscleGroups) { group ->
                    FilterChip(
                        selected = libraryState.selectedMuscleGroupId == group.id,
                        onClick = { viewModel.filterByMuscleGroup(group.id) },
                        label = { Text(group.name) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Blue,
                            selectedLabelColor = Color.White,
                            containerColor = SurfaceVariant,
                            labelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when {
                libraryState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Blue)
                    }
                }
                libraryState.errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(libraryState.errorMessage!!, color = Error, fontSize = 14.sp)
                    }
                }
                filteredExercises.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No exercises found", color = Subtext, fontSize = 14.sp)
                    }
                }
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(filteredExercises) { exercise ->
                            ExerciseLibraryCard(exercise = exercise)
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExerciseLibraryCard(exercise: ExerciseLibraryResponse) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Surface)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = exercise.name,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
            if (!exercise.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = exercise.description,
                    color = Subtext,
                    fontSize = 12.sp,
                    maxLines = 2
                )
            }
            if (exercise.muscle_groups.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    exercise.muscle_groups.take(3).forEach { mg ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(SurfaceVariant)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(mg, color = Subtext, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}