package com.example.lightweight.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lightweight.navigation.Screen
import com.example.lightweight.ui.theme.Blue
import com.example.lightweight.ui.theme.Surface

@Composable
fun LightWeightBottomBar(
    currentScreen: String,
    onNavigateTo: (String) -> Unit = {}
) {
    NavigationBar(
        containerColor = Surface,
        contentColor = Color.Gray,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            BottomNavItem("Community", Icons.Default.Search, Screen.Community.route),
            BottomNavItem("My Plans", Icons.Default.List, Screen.MyPlans.route),
            BottomNavItem("Calendar", Icons.Default.DateRange, Screen.Calendar.route),
            BottomNavItem("Profile", Icons.Default.Person, Screen.Profile.route)
        )

        items.forEach { item ->
            val isSelected = currentScreen == item.label
            NavigationBarItem(
                selected = isSelected,
                onClick = { if (!isSelected) onNavigateTo(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (isSelected) Blue else Color.Gray
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (isSelected) Blue else Color.Gray,
                        fontSize = 12.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

data class BottomNavItem(val label: String, val icon: ImageVector, val route: String)