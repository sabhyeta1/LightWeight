package com.example.lightweight.ui.screens.community

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lightweight.data.remote.CommunityPlanResponse
import com.example.lightweight.ui.components.LightWeightBottomBar
import com.example.lightweight.ui.components.LightWeightHeader
import com.example.lightweight.ui.theme.Background
import com.example.lightweight.ui.theme.Blue
import com.example.lightweight.ui.theme.Surface
import com.example.lightweight.ui.viewmodel.CommunityViewModel

@Composable
fun SavedPlansScreen(
    onNavigateTo: (String) -> Unit = {},
    onViewPlan: (Int) -> Unit = {},
    onBack: () -> Unit = {},
    viewModel: CommunityViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadSavedPlans()
    }

    // Dialog State
    var planToCopy by remember { mutableStateOf<CommunityPlanResponse?>(null) }
    var copiedPlanName by remember { mutableStateOf<String?>(null) }
    var errorToShow by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState.copySuccessMessage) {
        if (uiState.copySuccessMessage != null) {
            copiedPlanName = uiState.copySuccessMessage
            viewModel.clearMessages()
        }
    }
    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            errorToShow = uiState.errorMessage
            viewModel.clearMessages()
        }
    }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Saved Plans",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            when {
                uiState.isLoadingSaved -> {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Blue)
                    }
                }
                uiState.savedPlans.isEmpty() -> {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No saved plans yet. Tap the heart on a community plan to save it.",
                            color = Color.Gray
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.savedPlans) { plan ->
                            CommunityPlanItem(
                                plan = plan,
                                onAddToMyPlans = { planToCopy = plan },
                                onViewPlan = { onViewPlan(plan.id) },
                                isSaved = plan.id in uiState.savedPlanIds,
                                onToggleSave = { viewModel.toggleSavePlan(plan.id) }
                            )
                        }
                    }
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
                Text("Back", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    // Bestätigung vorm kopieren
    planToCopy?.let { plan ->
        AlertDialog(
            onDismissRequest = { planToCopy = null },
            containerColor = Surface,
            titleContentColor = Color.White,
            textContentColor = Color.Gray,
            title = { Text("Add to My Plans?") },
            text = { Text("\"${plan.name}\" by ${plan.owner_name} will be copied to your plans.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.copyPlan(plan.id)
                    planToCopy = null
                }) {
                    Text("Add", color = Blue, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { planToCopy = null }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    // Bestätigung nachm kopieren
    copiedPlanName?.let { name ->
        AlertDialog(
            onDismissRequest = { copiedPlanName = null },
            containerColor = Surface,
            titleContentColor = Color.White,
            textContentColor = Color.Gray,
            title = { Text("Plan added!") },
            text = { Text(name) },
            confirmButton = {
                TextButton(onClick = { copiedPlanName = null }) {
                    Text("OK", color = Blue, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Fehler Dialog
    errorToShow?.let { error ->
        AlertDialog(
            onDismissRequest = { errorToShow = null },
            containerColor = Surface,
            titleContentColor = Color.White,
            textContentColor = Color.Gray,
            title = { Text("Fehler") },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = { errorToShow = null }) {
                    Text("OK", color = Blue, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}