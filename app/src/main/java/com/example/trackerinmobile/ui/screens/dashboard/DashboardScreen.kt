package com.example.trackerinmobile.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trackerinmobile.R
import com.example.trackerinmobile.TrackerinApplication
import com.example.trackerinmobile.core.LocalBackStack
import com.example.trackerinmobile.core.Routes
import com.example.trackerinmobile.core.Todo
import com.example.trackerinmobile.core.TodoViewModel
import com.example.trackerinmobile.core.TodoViewModelFactory
import com.example.trackerinmobile.ui.components.CustomBottomNavigation
import com.example.trackerinmobile.ui.theme.*

@Composable
fun DashboardScreen() {
    val context = LocalContext.current
    val appContainer = (context.applicationContext as TrackerinApplication).container
    val viewModel: TodoViewModel = viewModel(
        factory = TodoViewModelFactory(appContainer.apiService)
    )

    val scrollState = rememberScrollState()
    val todos by viewModel.todos.collectAsState()
    var showTodoDialog by remember { mutableStateOf(false) }
    var editingTodoId by remember { mutableStateOf<String?>(null) }
    var todoInputValue by remember { mutableStateOf("") }
    var todoInputDesc by remember { mutableStateOf("") }
    var todoInputDue by remember { mutableStateOf("") }

    val backStack = LocalBackStack.current

    Scaffold(
        bottomBar = {
            CustomBottomNavigation(
                activeTab = 0,
                onTabSelected = { index ->
                    when (index) {
                        2 -> backStack.add(Routes.ProgressRoute)
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
            DashboardHeader()
            Spacer(modifier = Modifier.height(20.dp))
            StreakWidget()
            Spacer(modifier = Modifier.height(20.dp))
            ActiveCourseWidget()
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
            ActionButtonsRow()
            Spacer(modifier = Modifier.height(16.dp))
            CreateRoadmapWidget()
            Spacer(modifier = Modifier.height(16.dp))
            ProgressOverviewWidget()
            Spacer(modifier = Modifier.height(16.dp))
            WeeklyProgressChartWidget()
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
fun DashboardHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Temporary Hardcoded Profile Picture (Placeholder)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue),
                contentAlignment = Alignment.Center
            ) {
                Text("V", color = WhitePure, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Hi, Vinneth!",
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
                    .background(BlackishBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(painter = painterResource(id = R.drawable.plus_icon), contentDescription = "Add", tint = WhitePure, modifier = Modifier.size(20.dp))
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
fun StreakWidget() {
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
                text = "14 Days Streak",
                color = PrimaryBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ActiveCourseWidget() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(PrimaryBlue)
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "Fullstack Web Development",
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
                    text = "74",
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
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .border(2.dp, PrimaryBlue, CircleShape)
                .background(if (todo.isCompleted) PrimaryBlue else Color.Transparent)
                .clickable { onToggleComplete() },
            contentAlignment = Alignment.Center
        ) {
            if (todo.isCompleted) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(WhitePure))
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = todo.title,
            fontSize = 14.sp,
            color = if (todo.isCompleted) TextGray else Black,
            textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
            modifier = Modifier.weight(1f).clickable { onClick() }
        )
    }
}

@Composable
fun ActionButtonsRow() {
    val backStack = LocalBackStack.current
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(
            onClick = { /*TODO*/ },
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
fun CreateRoadmapWidget() {
    // Customization: Disesuaikan agar lebih serasi, tanpa icon bintang
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ComponentGray.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .background(WhitePure, RoundedCornerShape(16.dp))
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
fun ProgressOverviewWidget() {
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

            // Customized Segmented Button untuk Weekly/Monthly agar lebih serasi
            Row(
                modifier = Modifier
                    .background(BackgroundApp, RoundedCornerShape(8.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(WhitePure, RoundedCornerShape(6.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Weekly", fontSize = 12.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Monthly", fontSize = 12.sp, color = TextGray, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Total Hours", fontSize = 14.sp, color = Black)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("61.5", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Black)
                    Text(" h", fontSize = 16.sp, color = Black, modifier = Modifier.padding(bottom = 4.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Daily Average", fontSize = 14.sp, color = Black)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("4.1", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Black)
                    Text(" h", fontSize = 16.sp, color = Black, modifier = Modifier.padding(bottom = 4.dp))
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text("Topics Completed", fontSize = 14.sp, color = Black)
                Text("15", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Black)

                Spacer(modifier = Modifier.height(16.dp))

                Text("Days Active", fontSize = 14.sp, color = Black)
                Text("4", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Black)
            }
        }
    }
}

@Composable
fun WeeklyProgressChartWidget() {
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

        // Mock Chart
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            val chartLightBlue = Color(0xFFAAC4FF)

            ChartBar(day = "M", height = 70f, color = chartLightBlue)
            ChartBar(day = "T", height = 65f, color = chartLightBlue)
            ChartBar(day = "W", height = 30f, color = chartLightBlue)
            ChartBar(day = "T", height = 60f, color = chartLightBlue)
            ChartBar(day = "F", height = 100f, color = PrimaryBlue) // Active Day
            ChartBar(day = "S", height = 50f, color = chartLightBlue)
            ChartBar(day = "S", height = 10f, color = chartLightBlue)
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


