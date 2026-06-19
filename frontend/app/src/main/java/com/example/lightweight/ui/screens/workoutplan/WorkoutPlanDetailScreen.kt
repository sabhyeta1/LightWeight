package com.example.lightweight.ui.screens.workoutplan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.lightweight.data.remote.ExerciseInPlanResponse
import com.example.lightweight.data.remote.ExerciseSetResponse
import com.example.lightweight.data.remote.WorkoutPlanDetailResponse
import com.example.lightweight.ui.components.LightWeightBottomBar
import com.example.lightweight.ui.components.LightWeightHeader
import com.example.lightweight.ui.theme.Background
import com.example.lightweight.ui.theme.Blue
import com.example.lightweight.ui.theme.LightWeightTheme
import com.example.lightweight.ui.theme.Surface

@Composable
fun WorkoutPlanDetailScreen(
    plan: WorkoutPlanDetailResponse?,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onNavigateTo: (String) -> Unit = {},
    onBack: () -> Unit = {}
) {
    Scaffold(
        topBar = { LightWeightHeader() },
        bottomBar = {
            LightWeightBottomBar(
                currentScreen = "My Plans",
                onNavigateTo = onNavigateTo
            )
        },
        containerColor = Background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Blue
                )
            } else if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (plan != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        item {
                            Column {
                                Text(
                                    text = plan.name,
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                plan.description?.let { desc ->
                                    if (desc.isNotEmpty()) {
                                        Text(
                                            text = desc,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            }
                        }

                        items(plan.exercises) { exercise ->
                            ExerciseCard(exercise)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Blue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Exit",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseCard(exercise: ExerciseInPlanResponse) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Surface)
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        val imageUrl = exercise.photo_url?.let { "http://10.0.2.2:3000$it" }

        AsyncImage(
            model = imageUrl,
            contentDescription = exercise.name,
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            error = painterResource(id = android.R.drawable.ic_menu_gallery),
            placeholder = painterResource(id = android.R.drawable.ic_menu_gallery)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = exercise.name,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(4.dp))

            if (exercise.sets.isNotEmpty()) {
                val firstSet = exercise.sets.first()
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val details = mutableListOf<String>()
                    details.add("Sets: ${exercise.sets.size}")
                    firstSet.reps?.let { details.add("Reps: $it") }
                    firstSet.weight?.let { details.add("Weight: $it kg") }

                    Text(
                        text = details.joinToString(" | "),
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )

                    firstSet.machine_settings?.let { settings ->
                        if (settings.isNotEmpty()) {
                            Text(
                                text = "[$settings]",
                                color = Blue,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = "no sets defined",
                    color = Color.DarkGray,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
fun WorkoutPlanDetailScreenPreview() {
    val samplePlan = WorkoutPlanDetailResponse(
        id = 1,
        name = "Oberkörper Fokus",
        description = "Intensives Training für Brust und Rücken",
        is_published = false,
        exercises = listOf(
            ExerciseInPlanResponse(
                ewp_id = 1, order = 1, exercise_id = 1, name = "Bankdrücken", photo_url = null,
                sets = listOf(
                    ExerciseSetResponse(id = 1, set_number = 1, reps = 10, weight = 60.0, machine_settings = null, is_drop_set = false),
                    ExerciseSetResponse(id = 2, set_number = 2, reps = 8, weight = 65.0, machine_settings = null, is_drop_set = false)
                )
            ),
            ExerciseInPlanResponse(
                ewp_id = 2, order = 2, exercise_id = 2, name = "Beinpresse", photo_url = null,
                sets = listOf(
                    ExerciseSetResponse(id = 3, set_number = 1, reps = 12, weight = 120.0, machine_settings = "Sitz Pos. 3", is_drop_set = false)
                )
            )
        )
    )
    LightWeightTheme {
        WorkoutPlanDetailScreen(
            plan = samplePlan
        )
    }
}
