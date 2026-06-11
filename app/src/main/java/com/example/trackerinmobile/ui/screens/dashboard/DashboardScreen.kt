package com.example.trackerinmobile.ui.screens.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.trackerinmobile.R
import com.example.trackerinmobile.core.LocalBackStack
import java.util.Locale
import java.text.SimpleDateFormat
import java.util.Date
import com.example.trackerinmobile.core.Routes
import com.example.trackerinmobile.core.Todo
import com.example.trackerinmobile.core.TodoViewModel
import com.example.trackerinmobile.data.local.TokenManager
import com.example.trackerinmobile.ui.components.CustomBottomNavigation
import com.example.trackerinmobile.ui.screens.auth.AuthViewModel
import com.example.trackerinmobile.ui.theme.*

@Composable
fun DashboardScreen() {
    val authViewModel: AuthViewModel = hiltViewModel()
    val userName = remember { authViewModel.tokenManager.getUserName()?.split(" ")?.firstOrNull() ?: "User" }
    
    val viewModel: TodoViewModel = hiltViewModel()

    val scrollState = rememberScrollState()
    val todos by viewModel.todos.collectAsState()
    var showTodoDialog by remember { mutableStateOf(false) }
    var editingTodoId by remember { mutableStateOf<String?>(null) }
    var todoInputValue by remember { mutableStateOf("") }
    var todoInputDesc by remember { mutableStateOf("") }
    var todoInputDue by remember { mutableStateOf("") }

    val activeTitle by viewModel.curriculumTitle.collectAsState()
    val activeProgress by viewModel.curriculumProgress.collectAsState()
    val activeId by viewModel.curriculumId.collectAsState()

    val topicsCompleted by viewModel.topicsCompleted.collectAsState()
    val totalHours by viewModel.totalHours.collectAsState()
    val daysActive by viewModel.daysActive.collectAsState()
    val dailyAverage by viewModel.dailyAverage.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()

    val backStack = LocalBackStack.current

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    Scaffold(
        bottomBar = {
            CustomBottomNavigation(
                activeTab = 0,
                onTabSelected = { index ->
                    when (index) {
                        1 -> backStack.add(Routes.ExploreRoute)
                        2 -> backStack.add(Routes.ProgressRoute)
                        3 -> backStack.add(Routes.ProfileRoute)
                    }
                }
            )
        },
        containerColor = BackgroundApp
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            DashboardHeader(userName)
            Spacer(modifier = Modifier.height(20.dp))
            StreakWidget(streak = viewModel.tokenManager.getCurrentStreak())
            Spacer(modifier = Modifier.height(20.dp))
            ActiveCourseWidget(
                title = activeTitle,
                progress = activeProgress,
                onClick = {
                    activeId?.let { id ->
                        backStack.add(Routes.CurriculumDetailRoute(curriculumId = id))
                    } ?: run {
                        backStack.add(Routes.ExploreRoute)
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            TasksWidget(
                todos = todos,
                onAddClick = {
                    editingTodoId = null
                    todoInputValue = ""
                    todoInputDesc = ""
                    todoInputDue = ""
                    showTodoDialog = true
                },
                onToggleComplete = { id -> viewModel.toggleTodoCompleted(id) },
                onTaskClick = { todo ->
                    editingTodoId = todo.id
                    todoInputValue = todo.title
                    todoInputDesc = todo.description
                    todoInputDue = todo.dueDate
                    showTodoDialog = true
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            ActionButtonsRow(
                onClickContinue = {
                    activeId?.let { id ->
                        backStack.add(Routes.CurriculumDetailRoute(curriculumId = id))
                    } ?: run {
                        backStack.add(Routes.ExploreRoute)
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            CreateRoadmapWidget(
                onClick = { backStack.add(Routes.ExploreRoute) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            ProgressOverviewWidget(
                totalHours = totalHours,
                topicsCompleted = topicsCompleted,
                daysActive = daysActive,
                dailyAverage = dailyAverage,
                selectedFilter = selectedFilter,
                onFilterSelected = { filter -> viewModel.setFilter(filter) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            WeeklyProgressChartWidget(tokenManager = viewModel.tokenManager)
            Spacer(modifier = Modifier.height(32.dp))
        }

        if (showTodoDialog) {
            AlertDialog(
                onDismissRequest = { showTodoDialog = false },
                title = { Text(if (editingTodoId == null) "Add Task" else "Edit Task") },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = todoInputValue,
                            onValueChange = { todoInputValue = it },
                            singleLine = true,
                            label = { Text("Task Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = todoInputDesc,
                            onValueChange = { todoInputDesc = it },
                            singleLine = true,
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = todoInputDue,
                            onValueChange = { todoInputDue = it },
                            singleLine = true,
                            label = { Text("Due Date (e.g. Deadline 6/7/2026)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (todoInputValue.isNotBlank()) {
                            if (editingTodoId == null) {
                                viewModel.addTodo(todoInputValue, todoInputDesc, todoInputDue)
                            } else {
                                viewModel.updateTodo(editingTodoId!!, todoInputValue, todoInputDesc, todoInputDue)
                            }
                        }
                        showTodoDialog = false
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    Row {
                        if (editingTodoId != null) {
                            TextButton(onClick = {
                                viewModel.deleteTodo(editingTodoId!!)
                                showTodoDialog = false
                            }) {
                                Text("Delete", color = Color.Red)
                            }
                        }
                        TextButton(onClick = { showTodoDialog = false }) {
                            Text("Cancel")
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun DashboardHeader(userName: String) {
    val backStack = LocalBackStack.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // User Profile Picture instead of Initials
            Image(
                painter = painterResource(id = R.drawable.temporary_profile),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(1.dp, ComponentGray.copy(alpha = 0.3f), CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Hi, $userName!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Black
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BlackishBlue)
                    .clickable { backStack.add(Routes.NotesRoute) },
                contentAlignment = Alignment.Center
            ) {
                Icon(painter = painterResource(id = R.drawable.plus_icon), contentDescription = "Add Notes", tint = WhitePure, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(WhitePure),
                contentAlignment = Alignment.Center
            ) {
                Icon(painter = painterResource(id = R.drawable.bell_icon), contentDescription = "Notifications", tint = Black, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun StreakWidget(streak: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50.dp))
            .background(WhitePure)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.fire_icon),
                contentDescription = "Streak",
                tint = PrimaryBlue,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$streak Days Streak",
                color = PrimaryBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ActiveCourseWidget(title: String, progress: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(PrimaryBlue)
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = title,
                    color = WhitePure,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(WhitePure),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_right),
                        contentDescription = "Go",
                        tint = Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$progress",
                    color = WhitePure,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = " %",
                    color = WhitePure,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Completed",
                    color = WhitePure.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}

@Composable
fun TasksWidget(
    todos: List<Todo>,
    onAddClick: () -> Unit,
    onToggleComplete: (String) -> Unit,
    onTaskClick: (Todo) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ComponentGray.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .background(WhitePure, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tasks to do",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Black
            )
            Text(
                text = "+ Add",
                color = PrimaryBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onAddClick() }.padding(4.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        todos.forEach { todo ->
            TaskItem(
                todo = todo,
                onToggleComplete = { onToggleComplete(todo.id) },
                onClick = { onTaskClick(todo) }
            )
        }
    }
}

@Composable
fun TaskItem(todo: Todo, onToggleComplete: () -> Unit, onClick: () -> Unit) {
    val isCompleted = todo.isCompleted
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = if (isCompleted) 4.dp else 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .border(2.dp, PrimaryBlue, CircleShape)
                    .background(if (isCompleted) PrimaryBlue else Color.Transparent)
                    .clickable { onToggleComplete() },
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(WhitePure))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = todo.title,
                fontSize = 14.sp,
                color = if (isCompleted) TextGray else Black,
                textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                modifier = Modifier.weight(1f)
            )
        }
        if (!isCompleted) {
            if (todo.description.isNotBlank() || todo.dueDate.isNotBlank()) {
                Column(modifier = Modifier.padding(start = 32.dp, top = 4.dp)) {
                    if (todo.description.isNotBlank()) {
                        Text(
                            text = todo.description,
                            fontSize = 12.sp,
                            color = TextGray,
                            lineHeight = 16.sp
                        )
                    }
                    if (todo.dueDate.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = todo.dueDate,
                            fontSize = 11.sp,
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActionButtonsRow(onClickContinue: () -> Unit) {
    val backStack = LocalBackStack.current
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(
            onClick = onClickContinue,
            modifier = Modifier
                .weight(1f)
                .height(60.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text("Continue\nLearning", textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
            onClick = { backStack.add(Routes.ProgressRoute) },
            modifier = Modifier
                .weight(1f)
                .height(60.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue),
            border = androidx.compose.foundation.BorderStroke(1.dp, ComponentGray.copy(alpha = 0.5f))
        ) {
            Text("Curriculum", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CreateRoadmapWidget(onClick: () -> Unit) {
    // Customization: Disesuaikan agar lebih serasi, tanpa icon bintang
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ComponentGray.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .background(WhitePure, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Create New Roadmap",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Use AI to design a personalized learning path.",
                fontSize = 14.sp,
                color = PrimaryBlue.copy(alpha = 0.8f),
                lineHeight = 20.sp
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Icon(
            painter = painterResource(id = R.drawable.plus_icon),
            contentDescription = "Create",
            tint = Black,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun ProgressOverviewWidget(
    totalHours: Double,
    topicsCompleted: Int,
    daysActive: Int,
    dailyAverage: Double,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ComponentGray.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .background(WhitePure, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Progress Overview",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Black
            )

            // Customized Segmented Button untuk Weekly/Monthly agar lebih serasi dan interactive
            Row(
                modifier = Modifier
                    .background(BackgroundApp, RoundedCornerShape(8.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(if (selectedFilter == "Weekly") WhitePure else Color.Transparent, RoundedCornerShape(6.dp))
                        .clickable { onFilterSelected("Weekly") }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Weekly", fontSize = 12.sp, color = if (selectedFilter == "Weekly") PrimaryBlue else TextGray, fontWeight = if (selectedFilter == "Weekly") FontWeight.Bold else FontWeight.Medium)
                }
                Box(
                    modifier = Modifier
                        .background(if (selectedFilter == "Monthly") WhitePure else Color.Transparent, RoundedCornerShape(6.dp))
                        .clickable { onFilterSelected("Monthly") }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Monthly", fontSize = 12.sp, color = if (selectedFilter == "Monthly") PrimaryBlue else TextGray, fontWeight = if (selectedFilter == "Monthly") FontWeight.Bold else FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Total Hours", fontSize = 14.sp, color = Black)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(String.format(Locale.US, "%.1f", totalHours), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Black)
                    Text(" h", fontSize = 16.sp, color = Black, modifier = Modifier.padding(bottom = 4.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Daily Average", fontSize = 14.sp, color = Black)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(String.format(Locale.US, "%.1f", dailyAverage), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Black)
                    Text(" h", fontSize = 16.sp, color = Black, modifier = Modifier.padding(bottom = 4.dp))
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text("Topics Completed", fontSize = 14.sp, color = Black)
                Text("$topicsCompleted", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Black)

                Spacer(modifier = Modifier.height(16.dp))

                Text("Days Active", fontSize = 14.sp, color = Black)
                Text("$daysActive", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Black)
            }
        }
    }
}

@Composable
fun WeeklyProgressChartWidget(tokenManager: TokenManager) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ComponentGray.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .background(WhitePure, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Text(
            text = "Weekly Progress",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Get actual values from TokenManager
        val monVal = tokenManager.getDailyActivity("MON")
        val tueVal = tokenManager.getDailyActivity("TUE")
        val wedVal = tokenManager.getDailyActivity("WED")
        val thuVal = tokenManager.getDailyActivity("THU")
        val friVal = tokenManager.getDailyActivity("FRI")
        val satVal = tokenManager.getDailyActivity("SAT")
        val sunVal = tokenManager.getDailyActivity("SUN")

        // Find current day of the week to highlight it
        val currentDay = SimpleDateFormat("EEE", Locale.US).format(Date()).uppercase()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            val chartLightBlue = Color(0xFFAAC4FF)

            ChartBar(day = "M", height = monVal, color = if (currentDay == "MON") PrimaryBlue else chartLightBlue)
            ChartBar(day = "T", height = tueVal, color = if (currentDay == "TUE") PrimaryBlue else chartLightBlue)
            ChartBar(day = "W", height = wedVal, color = if (currentDay == "WED") PrimaryBlue else chartLightBlue)
            ChartBar(day = "T", height = thuVal, color = if (currentDay == "THU") PrimaryBlue else chartLightBlue)
            ChartBar(day = "F", height = friVal, color = if (currentDay == "FRI") PrimaryBlue else chartLightBlue)
            ChartBar(day = "S", height = satVal, color = if (currentDay == "SAT") PrimaryBlue else chartLightBlue)
            ChartBar(day = "S", height = sunVal, color = if (currentDay == "SUN") PrimaryBlue else chartLightBlue)
        }
    }
}

@Composable
fun ChartBar(day: String, height: Float, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .width(28.dp)
                .height(height.dp)
                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = day, fontSize = 12.sp, color = Black)
    }
}
