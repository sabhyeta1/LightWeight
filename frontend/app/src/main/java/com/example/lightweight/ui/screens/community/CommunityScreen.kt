package com.example.lightweight.ui.screens.community

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lightweight.data.remote.CommunityPlanResponse
import com.example.lightweight.ui.components.LightWeightBottomBar
import com.example.lightweight.ui.components.LightWeightHeader
import com.example.lightweight.ui.theme.*
import com.example.lightweight.ui.viewmodel.CommunityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    onNavigateTo: (String) -> Unit = {},
    onViewPlan: (Int) -> Unit = {},
    viewModel: CommunityViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("name") }
    val filterOptions = listOf("name", "muscle group")

    // suche bei jeder änderung von searchQuery oder selectedFilter
    LaunchedEffect(searchQuery, selectedFilter) {
        viewModel.onSearchChanged(searchQuery, selectedFilter)
    }

    // Dialog State - welcher Plan wird gerade kopiert
    var planToCopy by remember { mutableStateOf<CommunityPlanResponse?>(null) }
    // Erfolgsdialog nach dem kopieren
    var copiedPlanName by remember { mutableStateOf<String?>(null) }
    // Fehler als dialog anzeigen
    var errorToShow by remember { mutableStateOf<String?>(null) }

    // ViewModel state beobachten für Erfolg/Fehler
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
        bottomBar = { LightWeightBottomBar(currentScreen = "Community", onNavigateTo = onNavigateTo) },
        containerColor = Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Community",
                color = OnBackground,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search...", color = Subtext) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Subtext)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = OnBackground,
                        unfocusedTextColor = OnBackground,
                        cursorColor = Blue,
                        focusedBorderColor = Blue,
                        unfocusedBorderColor = SurfaceVariant,
                        focusedContainerColor = Surface,
                        unfocusedContainerColor = Surface,
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box {
                    Surface(
                        modifier = Modifier
                            .height(56.dp)
                            .clickable { expanded = true },
                        shape = RoundedCornerShape(12.dp),
                        color = Surface,
                        border = BorderStroke(1.dp, SurfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = selectedFilter, fontSize = 14.sp, color = OnSurface)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = OnSurface)
                        }
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(SurfaceVariant)
                    ) {
                        filterOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option, color = OnSurface) },
                                onClick = {
                                    selectedFilter = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Blue)
                    }
                }
                uiState.plans.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No plans found.", color = Subtext, fontSize = 16.sp)
                    }
                }
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(uiState.plans) { plan ->
                            CommunityPlanItem(
                                plan = plan,
                                onAddToMyPlans = { planToCopy = plan },
                                onViewPlan = { onViewPlan(plan.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Bestätigung vor dem kopieren
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

    // Bestätiung nach dem kopieren
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

@Composable
fun CommunityPlanItem(
    plan: CommunityPlanResponse,
    onAddToMyPlans: () -> Unit,
    onViewPlan: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface, RoundedCornerShape(12.dp))
            .clickable { onViewPlan() }
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = plan.name, color = OnBackground, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Text(text = "by ${plan.owner_name}", color = Subtext, fontSize = 13.sp)
        if (!plan.description.isNullOrBlank()) {
            Text(text = plan.description, color = Subtext, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onAddToMyPlans,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Blue),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Add to My Plans", color = Color.White, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommunityScreenPreview() {
    LightWeightTheme {
        CommunityScreen()
    }
}