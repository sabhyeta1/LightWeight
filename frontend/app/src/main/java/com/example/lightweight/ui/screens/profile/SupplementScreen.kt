package com.example.lightweight.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lightweight.data.remote.SupplementResponse
import com.example.lightweight.ui.components.LightWeightBottomBar
import com.example.lightweight.ui.components.LightWeightHeader
import com.example.lightweight.ui.theme.Background
import com.example.lightweight.ui.theme.Blue
import com.example.lightweight.ui.theme.Surface
import com.example.lightweight.ui.theme.SurfaceVariant
import com.example.lightweight.ui.viewmodel.SupplementViewModel

// FR-25: Supplements mit Name + Dosage anlegen und in Liste anzeigen
@Composable
fun SupplementsScreen(
    onNavigateTo: (String) -> Unit = {},
    onBack: () -> Unit = {},
    viewModel: SupplementViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var supplementToDelete by remember { mutableStateOf<SupplementResponse?>(null) }

    Scaffold(
        topBar = { LightWeightHeader() },
        bottomBar = {
            LightWeightBottomBar(
                currentScreen = "Profile",
                onNavigateTo = onNavigateTo
            )
        },
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
                text = "Supplements",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Blue,
                    unfocusedBorderColor = SurfaceVariant,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            OutlinedTextField(
                value = dosage,
                onValueChange = { dosage = it },
                label = { Text("Dosage (e.g. 500mg)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Blue,
                    unfocusedBorderColor = SurfaceVariant,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }

            if (name.isBlank() || dosage.isBlank()) {
                Text(
                    text = "Enter a name and dosage to save.",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }

            Button(
                onClick = {
                    viewModel.addSupplement(name, dosage)
                    name = ""
                    dosage = ""
                },
                enabled = name.isNotBlank() && dosage.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Blue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Add Supplement", color = Color.White, fontWeight = FontWeight.SemiBold)
            }

            HorizontalDivider(color = SurfaceVariant)

            Text(
                text = "Your Supplements",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )

            Box(modifier = Modifier.weight(1f)) {
                when {
                    uiState.isLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Blue)
                        }
                    }
                    uiState.supplements.isEmpty() -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "No supplements added yet.",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.supplements) { supplement ->
                                SupplementItem(
                                    supplement = supplement,
                                    onDelete = { supplementToDelete = supplement }
                                )
                            }
                        }
                    }
                }
            }

            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Back", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    supplementToDelete?.let { supplement ->
        AlertDialog(
            onDismissRequest = { supplementToDelete = null },
            containerColor = Surface,
            titleContentColor = Color.White,
            textContentColor = Color.Gray,
            title = { Text("Delete supplement?") },
            text = { Text("\"${supplement.name}\" will be removed from your supplement list.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteSupplement(supplement.id)
                    supplementToDelete = null
                }) {
                    Text("Delete", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { supplementToDelete = null }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun SupplementItem(
    supplement: SupplementResponse,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface, RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = supplement.name, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(text = supplement.dosage, color = Color.Gray, fontSize = 14.sp)
        }
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete ${supplement.name}",
                tint = Color.Gray
            )
        }
    }
}