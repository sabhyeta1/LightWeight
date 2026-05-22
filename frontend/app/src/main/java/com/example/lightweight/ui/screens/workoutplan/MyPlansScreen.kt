package com.example.lightweight.ui.screens.workoutplan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lightweight.ui.components.LightWeightHeader
import com.example.lightweight.ui.components.LightWeightBottomBar
import com.example.lightweight.ui.theme.Background
import com.example.lightweight.ui.theme.Blue
import com.example.lightweight.ui.theme.Surface
import com.example.lightweight.ui.theme.LightWeightTheme

@Composable
fun MyPlansScreen(
    onNavigateToCreate: () -> Unit = {},
    onEditPlan: (String) -> Unit = {},
    onDeletePlan: (String) -> Unit = {},
    onNavigateTo: (String) -> Unit = {}
) {
    val plans = remember { mutableStateListOf("Bulk Phase 1", "Summer Cut", "Strength Focus") }
    var planToDelete by remember { mutableStateOf<String?>(null) }

    var planToDelete by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = { LightWeightHeader() },
        bottomBar = {
            LightWeightBottomBar(
                currentScreen = "My Plans",
                onNavigateTo = onNavigateTo
            )
        },
        containerColor = Background,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "My Plans",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(plans) { plan ->
                    PlanItem(
                        name = plan,
                        onEdit = { onEditPlan(plan) },
                        onDelete = { planToDelete = plan }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onNavigateToCreate,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Blue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create New Plan", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    // Bestätigungs-Dialog — erscheint nur wenn planToDelete gesetzt ist
    planToDelete?.let { plan ->
        AlertDialog(
            onDismissRequest = { planToDelete = null },
            containerColor = Surface,
            titleContentColor = Color.White,
            textContentColor = Color.Gray,
            title = { Text("Delete \"$plan\"?") },
            text = { Text("This plan will be permanently removed. This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        plans.remove(plan)
                        onDeletePlan(plan)
                        planToDelete = null
                    }
                ) {
                    Text("Delete", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { planToDelete = null }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun PlanItem(name: String, onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
        Row {
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Blue)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
fun MyPlansScreenPreview() {
    LightWeightTheme {
        MyPlansScreen()
    }
}