package com.example.lightweight.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lightweight.data.remote.CalendarSessionResponse
import com.example.lightweight.data.remote.WorkoutPlanResponse
import com.example.lightweight.ui.components.LightWeightBottomBar
import com.example.lightweight.ui.components.LightWeightHeader
import com.example.lightweight.ui.theme.*
import com.example.lightweight.ui.viewmodel.CalendarViewModel
import com.example.lightweight.ui.viewmodel.WorkoutPlanViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

// Color palette (color_id 1–8)

val CALENDAR_COLORS = listOf(
    Color(0xFF4B7BEC), // 1 Blue (default)
    Color(0xFF26de81), // 2 Green
    Color(0xFFfd9644), // 3 Orange
    Color(0xFFfc5c65), // 4 Red
    Color(0xFFa55eea), // 5 Purple
    Color(0xFF45aaf2), // 6 Sky
    Color(0xFFf7b731), // 7 Yellow
    Color(0xFF2bcbba), // 8 Teal
)

enum class ScheduleTab {
    Upcoming,
    Completed
}

fun colorForId(colorId: Int): Color =
    CALENDAR_COLORS.getOrElse(colorId - 1) { CALENDAR_COLORS[0] }

fun parseSessionDateTime(session: CalendarSessionResponse): LocalDateTime {
    val dateString = session.session_date.substringBefore("T")
    val timeString = session.session_time.take(5)

    val date = LocalDate.parse(dateString)
    val time = LocalTime.parse(timeString)

    return LocalDateTime.of(date, time)
}

// Screen

@Composable
fun CalendarScreen(
    onNavigateTo: (String) -> Unit = {},
    calendarViewModel: CalendarViewModel = viewModel(),
    planViewModel: WorkoutPlanViewModel = viewModel()
) {
    val calendarState by calendarViewModel.calendarState.collectAsState()
    val planUiState   by planViewModel.uiState.collectAsState()

    var showScheduleDialog by remember { mutableStateOf(false) }
    var showDeleteDialog   by remember { mutableStateOf<CalendarSessionResponse?>(null) }
    var showEditDialog     by remember { mutableStateOf<CalendarSessionResponse?>(null) }
    var selectedTab        by remember { mutableStateOf(ScheduleTab.Upcoming) }

    // Auto-clear feedback messages
    LaunchedEffect(calendarState.successMessage, calendarState.errorMessage) {
        if (calendarState.successMessage != null || calendarState.errorMessage != null) {
            kotlinx.coroutines.delay(2500)
            calendarViewModel.clearMessages()
        }
    }

    val now = LocalDateTime.now()

    val upcomingSessions = remember(calendarState.sessions, now) {
        calendarState.sessions
            .filter { parseSessionDateTime(it) >= now }
            .sortedBy { parseSessionDateTime(it) }
    }

    val completedSessions = remember(calendarState.sessions, now) {
        calendarState.sessions
            .filter { parseSessionDateTime(it) < now }
            .sortedByDescending { parseSessionDateTime(it) }
    }

    val displayedSessions = when (selectedTab) {
        ScheduleTab.Upcoming -> upcomingSessions
        ScheduleTab.Completed -> completedSessions
    }

    val grouped: Map<String, List<CalendarSessionResponse>> = remember(displayedSessions) {
        displayedSessions.groupBy { it.session_date.take(10) }
    }

    val sortedDates: List<String> = remember(grouped, selectedTab) {
        if (selectedTab == ScheduleTab.Upcoming) {
            grouped.keys.sorted()
        } else {
            grouped.keys.sortedDescending()
        }
    }

    Scaffold(
        topBar = { LightWeightHeader() },
        bottomBar = { LightWeightBottomBar(currentScreen = "Calendar", onNavigateTo = onNavigateTo) },
        containerColor = Background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showScheduleDialog = true },
                containerColor = Blue,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Schedule workout")
            }
        }
    ) { paddingValues ->

        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            // Screen title
            Text(
                text = "Schedule",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            // Feedback banner
            calendarState.successMessage?.let { msg ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF26de81).copy(alpha = 0.12f))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(msg, color = Color(0xFF26de81), fontSize = 13.sp)
                }
            }
            calendarState.errorMessage?.let { msg ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Error.copy(alpha = 0.12f))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(msg, color = Error, fontSize = 13.sp)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextButton(
                    onClick = { selectedTab = ScheduleTab.Upcoming }
                ) {
                    Text(
                        text = "Upcoming",
                        color = if (selectedTab == ScheduleTab.Upcoming) Blue else Subtext,
                        fontWeight = if (selectedTab == ScheduleTab.Upcoming) FontWeight.Bold else FontWeight.Normal
                    )
                }

                TextButton(
                    onClick = { selectedTab = ScheduleTab.Completed }
                ) {
                    Text(
                        text = "Completed",
                        color = if (selectedTab == ScheduleTab.Completed) Blue else Subtext,
                        fontWeight = if (selectedTab == ScheduleTab.Completed) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            // Schedule list
            when {
                calendarState.isLoading && calendarState.sessions.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Blue)
                    }
                }

                sortedDates.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(bottom = 80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (selectedTab == ScheduleTab.Upcoming) {
                                    "No upcoming workouts"
                                } else {
                                    "No completed workouts"
                                },
                                color = Subtext,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Tap + to schedule your first session",
                                color = Subtext.copy(alpha = 0.6f),
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.dp, end = 16.dp,
                            top = 4.dp, bottom = 96.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        sortedDates.forEach { dateStr ->
                            val sessionsOnDay = grouped[dateStr] ?: return@forEach
                            val date = LocalDate.parse(dateStr.take(10))

                            // Date header
                            item(key = "header_$dateStr") {
                                DateHeader(date = date)
                            }

                            // Session rows for that day
                            items(
                                items = sessionsOnDay,
                                key = { "session_${it.id}" }
                            ) { session ->
                                ScheduleSessionRow(
                                    session = session,
                                    showActions = selectedTab == ScheduleTab.Upcoming,
                                    onEdit   = { showEditDialog = session },
                                    onDelete = { showDeleteDialog = session }
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                            }

                            item(key = "spacer_$dateStr") {
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }

                        // Load more button
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                OutlinedButton(
                                    onClick = { calendarViewModel.loadMore() },
                                    border = ButtonDefaults.outlinedButtonBorder.copy(
                                        // subtle border
                                    ),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Subtext)
                                ) {
                                    if (calendarState.isLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            color = Subtext,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text("Load more", fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Schedule dialog
    if (showScheduleDialog) {
        ScheduleWorkoutDialog(
            plans = planUiState.plans,
            onDismiss = { showScheduleDialog = false },
            onScheduleSingle = { planId, date, time, colorId ->
                calendarViewModel.scheduleSession(planId, date, time, colorId)
                showScheduleDialog = false
            },
            onScheduleRecurrence = { planId, type, weekdays, intervalDays, start, end, time, colorId ->
                calendarViewModel.scheduleRecurrence(planId, type, weekdays, intervalDays, start, end, time, colorId)
                showScheduleDialog = false
            }
        )
    }

    // Delete dialog
    showDeleteDialog?.let { session ->
        DeleteSessionDialog(
            session = session,
            onDismiss = { showDeleteDialog = null },
            onDeleteSingle = {
                calendarViewModel.deleteSession(session.id)
                showDeleteDialog = null
            },
            onDeleteSeries = {
                session.recurrence_rule_id?.let { rid -> calendarViewModel.deleteRecurrence(rid) }
                showDeleteDialog = null
            }
        )
    }

    // Edit dialog
    showEditDialog?.let { session ->
        EditSessionDialog(
            session = session,
            onDismiss = { showEditDialog = null },
            onSaveThisOnly = { date, time, colorId ->
                calendarViewModel.updateSession(session.id, date, time, colorId)
                showEditDialog = null
            },
            onSaveAllFuture = { time, colorId ->
                session.recurrence_rule_id?.let { rid ->
                    calendarViewModel.updateFutureSessions(
                        rid,
                        session.session_date.take(10),
                        time,
                        colorId
                    )
                }
                showEditDialog = null
            }
        )
    }
}

// Date header

@Composable
private fun DateHeader(date: LocalDate) {
    val today     = LocalDate.now()
    val tomorrow  = today.plusDays(1)

    val dayLabel = when (date) {
        today    -> "Today"
        tomorrow -> "Tomorrow"
        else     -> date.format(DateTimeFormatter.ofPattern("EEE"))  // "Mon"
    }
    val fullLabel = date.format(DateTimeFormatter.ofPattern("d MMM"))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = dayLabel,
            color = if (date == today) Blue else Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.width(72.dp)
        )
        Text(
            text = fullLabel,
            color = Subtext,
            fontSize = 13.sp
        )
    }
}

// Session row

@Composable
private fun ScheduleSessionRow(
    session: CalendarSessionResponse,
    showActions: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val color       = colorForId(session.color_id)
    val timeDisplay = session.session_time.take(5)   // "HH:MM"
    val isRecurring = session.recurrence_rule_id != null

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.15f))
            .border(
                width = 1.dp,
                color  = color.copy(alpha = 0.35f),
                shape  = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left color bar
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(32.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Plan name + meta
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = session.workout_plan_name,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = timeDisplay,
                    color = color,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                if (isRecurring) {
                    Text(
                        text = "  ·  Recurring",
                        color = Subtext,
                        fontSize = 12.sp
                    )
                }
            }
        }

        if (showActions) {
            // Edit icon
            IconButton(
                onClick = onEdit,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit session",
                    tint = Subtext,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Delete icon
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete session",
                    tint = Subtext,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// Delete dialog

@Composable
private fun DeleteSessionDialog(
    session: CalendarSessionResponse,
    onDismiss: () -> Unit,
    onDeleteSingle: () -> Unit,
    onDeleteSeries: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        title = {
            Text("Remove workout", color = Color.White, fontWeight = FontWeight.Bold)
        },
        text = {
            Text(
                if (session.recurrence_rule_id != null)
                    "\"${session.workout_plan_name}\" is part of a recurring series. Remove just this day, or the entire series?"
                else
                    "Remove \"${session.workout_plan_name}\" on ${session.session_date}?",
                color = Subtext,
                fontSize = 14.sp
            )
        },
        confirmButton = {
            if (session.recurrence_rule_id != null) {
                TextButton(onClick = onDeleteSeries) {
                    Text("Delete series", color = Error, fontWeight = FontWeight.SemiBold)
                }
            } else {
                TextButton(onClick = onDeleteSingle) {
                    Text("Delete", color = Error, fontWeight = FontWeight.SemiBold)
                }
            }
        },
        dismissButton = {
            Row {
                if (session.recurrence_rule_id != null) {
                    TextButton(onClick = onDeleteSingle) {
                        Text("This day only", color = Blue)
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = Subtext)
                }
            }
        }
    )
}

// Edit session dialog

@Composable
private fun EditSessionDialog(
    session: CalendarSessionResponse,
    onDismiss: () -> Unit,
    onSaveThisOnly: (date: String, time: String, colorId: Int) -> Unit,
    onSaveAllFuture: (time: String, colorId: Int) -> Unit
) {
    val isRecurring = session.recurrence_rule_id != null

    // For recurring sessions we first ask scope, then show the edit form
    var editAllFuture by remember { mutableStateOf<Boolean?>(if (isRecurring) null else false) }

    // If we haven't picked a scope yet, show the scope picker (mirrors delete dialog)
    if (editAllFuture == null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = Surface,
            title = {
                Text("Edit workout", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "\"${session.workout_plan_name}\" is part of a recurring series. " +
                            "Edit just this event, or all future events?",
                    color = Subtext,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { editAllFuture = true }) {
                    Text("All future events", color = Blue, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = { editAllFuture = false }) {
                        Text("This event only", color = Blue)
                    }
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Subtext)
                    }
                }
            }
        )
        return
    }

    // Edit form — pre-filled from current session
    var editDate        by remember { mutableStateOf(LocalDate.parse(session.session_date.take(10))) }
    var timeHour        by remember { mutableStateOf(session.session_time.take(2).toIntOrNull() ?: 18) }
    var timeMinute      by remember { mutableStateOf(session.session_time.drop(3).take(2).toIntOrNull() ?: 0) }
    var selectedColorId by remember { mutableStateOf(session.color_id) }

    val dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val scope   = if (editAllFuture == true) "all future events" else "this event"

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        modifier = Modifier.fillMaxWidth(),
        title = {
            Text(
                "Edit \"${session.workout_plan_name}\"",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Scope reminder
                if (isRecurring) {
                    Text(
                        "Editing $scope",
                        color = Blue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Date — only shown for single-event edits
                if (editAllFuture == false) {
                    SectionLabel("Date")
                    DateStepper(date = editDate, onDateChange = { editDate = it })
                }

                SectionLabel("Time")
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NumberStepper(value = timeHour, min = 0, max = 23, onValueChange = { timeHour = it }) { onTap ->
                        Text(
                            text = timeHour.toString().padStart(2, '0'),
                            color = Blue, fontSize = 22.sp, fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { onTap() }
                        )
                    }
                    Text(":", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    NumberStepper(value = timeMinute, min = 0, max = 59, step = 5, onValueChange = { timeMinute = it }) { onTap ->
                        Text(
                            text = timeMinute.toString().padStart(2, '0'),
                            color = Blue, fontSize = 22.sp, fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { onTap() }
                        )
                    }
                }

                SectionLabel("Color")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CALENDAR_COLORS.forEachIndexed { index, color ->
                        val cid = index + 1
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(color)
                                .then(
                                    if (selectedColorId == cid) Modifier.border(2.dp, Color.White, CircleShape)
                                    else Modifier
                                )
                                .clickable { selectedColorId = cid }
                        )
                    }
                }
            }
        },
        confirmButton = {
            val timeStr = "${timeHour.toString().padStart(2, '0')}:${timeMinute.toString().padStart(2, '0')}"
            TextButton(onClick = {
                if (editAllFuture == true) {
                    onSaveAllFuture(timeStr, selectedColorId)
                } else {
                    onSaveThisOnly(editDate.format(dateFmt), timeStr, selectedColorId)
                }
            }) {
                Text("Save", color = Blue, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Subtext) }
        }
    )
}

// Schedule dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleWorkoutDialog(
    plans: List<WorkoutPlanResponse>,
    onDismiss: () -> Unit,
    onScheduleSingle: (Int, LocalDate, String, Int) -> Unit,
    onScheduleRecurrence: (Int, String, List<Int>?, Int?, LocalDate, LocalDate, String, Int) -> Unit
) {
    var selectedPlanId  by remember { mutableStateOf(plans.firstOrNull()?.id) }
    var isRecurring     by remember { mutableStateOf(false) }
    var timeHour        by remember { mutableStateOf(18) }
    var timeMinute      by remember { mutableStateOf(0) }
    var selectedColorId by remember { mutableStateOf(1) }

    // Single
    var sessionDate by remember { mutableStateOf(LocalDate.now()) }

    // Recurrence
    var recurrenceType   by remember { mutableStateOf("weekdays") }
    var selectedWeekdays by remember { mutableStateOf(setOf<Int>()) }
    var intervalDays     by remember { mutableStateOf(2) }
    var startDate        by remember { mutableStateOf(LocalDate.now()) }
    var endDate          by remember { mutableStateOf(LocalDate.now().plusMonths(3)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        modifier = Modifier.fillMaxWidth(),
        title = {
            Text(
                "Schedule Workout",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                // Plan picker
                SectionLabel("Plan")
                if (plans.isEmpty()) {
                    Text("No plans yet — create one first.", color = Subtext, fontSize = 13.sp)
                } else {
                    var expanded by remember { mutableStateOf(false) }
                    val selectedPlan = plans.firstOrNull { it.id == selectedPlanId }
                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                        OutlinedTextField(
                            value = selectedPlan?.name ?: "Select a plan",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            colors = dialogTextFieldColors(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            containerColor = SurfaceVariant
                        ) {
                            plans.forEach { plan ->
                                DropdownMenuItem(
                                    text = { Text(plan.name, color = Color.White) },
                                    onClick = { selectedPlanId = plan.id; expanded = false }
                                )
                            }
                        }
                    }
                }

                // Recurring toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Recurring", color = Color.White, fontSize = 14.sp)
                    Switch(
                        checked = isRecurring,
                        onCheckedChange = { isRecurring = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Blue
                        )
                    )
                }

                if (!isRecurring) {
                    // Single: date picker
                    SectionLabel("Date")
                    DateStepper(date = sessionDate, onDateChange = { sessionDate = it })

                } else {
                    // Recurrence type tabs
                    SectionLabel("Repeat")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("weekdays" to "Specific days", "interval" to "Every X days").forEach { (key, label) ->
                            FilterChip(
                                selected = recurrenceType == key,
                                onClick  = { recurrenceType = key },
                                label    = { Text(label) },
                                colors   = dialogChipColors()
                            )
                        }
                    }

                    if (recurrenceType == "weekdays") {
                        SectionLabel("Days of week")
                        val dayMap = listOf(1 to "Mo", 2 to "Tu", 3 to "We", 4 to "Th", 5 to "Fr", 6 to "Sa", 0 to "Su")
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            dayMap.forEach { (idx, label) ->
                                val sel = idx in selectedWeekdays
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (sel) Blue else SurfaceVariant)
                                        .clickable {
                                            selectedWeekdays = if (sel) selectedWeekdays - idx
                                            else     selectedWeekdays + idx
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    } else {
                        SectionLabel("Interval")
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            IconButton(onClick = { if (intervalDays > 1) intervalDays-- }, modifier = Modifier.size(32.dp)) {
                                Text("−", color = Color.White, fontSize = 20.sp)
                            }
                            Text(
                                "Every $intervalDays day${if (intervalDays > 1) "s" else ""}",
                                color = Color.White, fontSize = 14.sp
                            )
                            IconButton(onClick = { if (intervalDays < 7) intervalDays++ }, modifier = Modifier.size(32.dp)) {
                                Text("+", color = Color.White, fontSize = 20.sp)
                            }
                        }
                    }

                    SectionLabel("Start date")
                    DateStepper(date = startDate, onDateChange = { startDate = it })

                    SectionLabel("End date")
                    DateStepper(date = endDate, onDateChange = { endDate = it })
                }

                // Time picker
                SectionLabel("Time")
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NumberStepper(value = timeHour, min = 0, max = 23, onValueChange = { timeHour = it }) { onTap ->
                        Text(
                            text = timeHour.toString().padStart(2, '0'),
                            color = Blue,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { onTap() }
                        )
                    }
                    Text(":", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    NumberStepper(value = timeMinute, min = 0, max = 59, step = 5, onValueChange = { timeMinute = it }) { onTap ->
                        Text(
                            text = timeMinute.toString().padStart(2, '0'),
                            color = Blue,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { onTap() }
                        )
                    }
                }

                // Color picker
                SectionLabel("Color")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CALENDAR_COLORS.forEachIndexed { index, color ->
                        val cid = index + 1
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(color)
                                .then(
                                    if (selectedColorId == cid)
                                        Modifier.border(2.dp, Color.White, CircleShape)
                                    else Modifier
                                )
                                .clickable { selectedColorId = cid }
                        )
                    }
                }
            }
        },
        confirmButton = {
            val timeStr = "${timeHour.toString().padStart(2, '0')}:${timeMinute.toString().padStart(2, '0')}"
            val canSchedule = selectedPlanId != null &&
                    (!isRecurring || recurrenceType != "weekdays" || selectedWeekdays.isNotEmpty())
            TextButton(
                onClick = {
                    val pid = selectedPlanId ?: return@TextButton
                    if (!isRecurring) {
                        onScheduleSingle(pid, sessionDate, timeStr, selectedColorId)
                    } else {
                        onScheduleRecurrence(
                            pid, recurrenceType,
                            if (recurrenceType == "weekdays") selectedWeekdays.toList() else null,
                            if (recurrenceType == "interval") intervalDays else null,
                            startDate, endDate, timeStr, selectedColorId
                        )
                    }
                },
                enabled = canSchedule
            ) {
                Text("Schedule", color = if (canSchedule) Blue else Subtext, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Subtext) }
        }
    )
}

// Tiny dialog helpers

@Composable
private fun SectionLabel(text: String) {
    Text(text, color = Subtext, fontSize = 12.sp, fontWeight = FontWeight.Medium)
}

@Composable
private fun dialogTextFieldColors() = OutlinedTextFieldDefaults.colors(
    unfocusedBorderColor = SurfaceVariant,
    focusedBorderColor   = Blue,
    unfocusedTextColor   = Color.White,
    focusedTextColor     = Color.White
)

@Composable
private fun dialogChipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = Blue,
    selectedLabelColor     = Color.White,
    containerColor         = SurfaceVariant,
    labelColor             = Color.White
)

// Date stepper — arrows + tap to open date picker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateStepper(date: LocalDate, onDateChange: (LocalDate) -> Unit) {
    val fmt = DateTimeFormatter.ofPattern("d MMM yyyy")
    var showPicker by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceVariant)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = { onDateChange(date.minusDays(1)) }) {
            Text("‹", color = Color.White, fontSize = 22.sp)
        }
        // Tap the date label to open the picker
        Text(
            text = date.format(fmt),
            color = Blue,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable { showPicker = true }
        )
        IconButton(onClick = { onDateChange(date.plusDays(1)) }) {
            Text("›", color = Color.White, fontSize = 22.sp)
        }
    }

    if (showPicker) {
        val epochDay = date.toEpochDay() * 86_400_000L
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = epochDay)
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        onDateChange(
                            Instant.ofEpochMilli(millis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                        )
                    }
                    showPicker = false
                }) { Text("OK", color = Blue) }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Cancel", color = Subtext) }
            },
            colors = DatePickerDefaults.colors(
                containerColor = Surface
            )
        ) {
            DatePicker(
                state = pickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = Surface,
                    titleContentColor = Color.White,
                    headlineContentColor = Color.White,
                    weekdayContentColor = Subtext,
                    subheadContentColor = Subtext,
                    navigationContentColor = Color.White,
                    yearContentColor = Color.White,
                    currentYearContentColor = Blue,
                    selectedYearContentColor = Color.White,
                    selectedYearContainerColor = Blue,
                    dayContentColor = Color.White,
                    selectedDayContentColor = Color.White,
                    selectedDayContainerColor = Blue,
                    todayContentColor = Blue,
                    todayDateBorderColor = Blue
                )
            )
        }
    }
}

// Number stepper — arrows + tap to type a value

@Composable
private fun NumberStepper(
    value: Int,
    min: Int,
    max: Int,
    step: Int = 1,
    onValueChange: (Int) -> Unit,
    content: @Composable (() -> Unit) -> Unit   // receives a "tap to edit" lambda
) {
    var showInput by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = { onValueChange(if (value + step > max) min else value + step) },
            modifier = Modifier.size(28.dp)
        ) { Text("▲", color = Subtext, fontSize = 12.sp) }

        content { showInput = true }

        IconButton(
            onClick = { onValueChange(if (value - step < min) max else value - step) },
            modifier = Modifier.size(28.dp)
        ) { Text("▼", color = Subtext, fontSize = 12.sp) }
    }

    if (showInput) {
        var text by remember { mutableStateOf(value.toString()) }
        AlertDialog(
            onDismissRequest = { showInput = false },
            containerColor = Surface,
            title = { Text("Enter value ($min–$max)", color = Color.White, fontSize = 15.sp) },
            text = {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it.filter { c -> c.isDigit() }.take(2) },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = SurfaceVariant,
                        focusedBorderColor = Blue,
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White,
                        cursorColor = Blue
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val parsed = text.toIntOrNull()
                    if (parsed != null && parsed in min..max) onValueChange(parsed)
                    showInput = false
                }) { Text("OK", color = Blue) }
            },
            dismissButton = {
                TextButton(onClick = { showInput = false }) { Text("Cancel", color = Subtext) }
            }
        )
    }
}