package com.example.lightweight.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lightweight.ui.components.LightWeightBottomBar
import com.example.lightweight.ui.components.LightWeightHeader
import com.example.lightweight.ui.theme.Background
import com.example.lightweight.ui.theme.Blue
import com.example.lightweight.ui.theme.Surface
import com.example.lightweight.ui.theme.SurfaceVariant
import com.example.lightweight.ui.viewmodel.WaterViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@Composable
fun WaterTrackingScreen(
    onNavigateTo: (String) -> Unit = {},
    onBack: () -> Unit = {},
    viewModel: WaterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showGoalDialog by remember { mutableStateOf(false) }
    var goalInput by remember { mutableStateOf("") }

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
                text = "Water",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Surface, RoundedCornerShape(16.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (uiState.targetMl != null) {
                            "Goal: ${formatLiters(uiState.targetMl!!)} L"
                        } else {
                            "No goal set"
                        },
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    IconButton(
                        onClick = {
                            goalInput = uiState.targetMl?.let { (it / 1000.0).toString() } ?: ""
                            showGoalDialog = true
                        },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit goal",
                            tint = Blue,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${formatLiters(uiState.totalMl)} L",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                val glassesPerRow = 6
                val totalSlots = uiState.logs.size + 1 // +1 für das "+"-Glas
                val rowCount = (totalSlots + glassesPerRow - 1) / glassesPerRow

                LazyColumn(
                    modifier = Modifier.heightIn(max = 200.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(rowCount) { rowIndex ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (col in 0 until glassesPerRow) {
                                val index = rowIndex * glassesPerRow + col
                                when {
                                    index < uiState.logs.size -> {
                                        val log = uiState.logs[index]
                                        IconButton(
                                            onClick = { viewModel.deleteIntake(log.id) },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            WaterGlass(filled = true)
                                        }
                                    }
                                    index == uiState.logs.size -> {
                                        IconButton(
                                            onClick = { viewModel.addGlass() },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            WaterGlass(filled = false, showPlus = true)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

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

    if (showGoalDialog) {
        AlertDialog(
            onDismissRequest = { showGoalDialog = false },
            containerColor = Surface,
            titleContentColor = Color.White,
            textContentColor = Color.Gray,
            title = { Text("Set daily goal") },
            text = {
                OutlinedTextField(
                    value = goalInput,
                    onValueChange = { goalInput = it },
                    label = { Text("Goal in litres (e.g. 2.2)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Blue,
                        unfocusedBorderColor = SurfaceVariant,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val liters = goalInput.replace(",", ".").toDoubleOrNull()
                    if (liters != null && liters > 0) {
                        viewModel.setGoal((liters * 1000).toInt())
                        showGoalDialog = false
                    }
                }) {
                    Text("Save", color = Blue, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showGoalDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun WaterGlass(filled: Boolean, showPlus: Boolean = false, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(36.dp)
            .clip(
                RoundedCornerShape(
                    topStart = 12.dp,
                    topEnd = 12.dp,
                    bottomStart = 6.dp,
                    bottomEnd = 6.dp
                )
            )
            .background(if (filled) Blue else SurfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (showPlus) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add glass (+250ml)",
                tint = Blue,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

fun formatLiters(ml: Int): String {
    return String.format("%.2f", ml / 1000.0)
}