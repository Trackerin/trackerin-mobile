package com.example.trackerinmobile.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.trackerinmobile.R
import com.example.trackerinmobile.core.LocalBackStack
import com.example.trackerinmobile.core.Routes
import com.example.trackerinmobile.core.Todo
import com.example.trackerinmobile.core.TodoViewModel
import com.example.trackerinmobile.ui.components.CustomBottomNavigation
import com.example.trackerinmobile.ui.theme.*

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import com.example.trackerinmobile.data.model.progress.CurriculumApiModel

@Composable
fun ProgressScreen() {
    val viewModel: TodoViewModel = hiltViewModel()
    val curriculumViewModel: CurriculumViewModel = hiltViewModel()

    val todos by viewModel.todos.collectAsState()
    val curriculums by curriculumViewModel.curriculums.collectAsState()
    val isLoadingCurriculums by curriculumViewModel.isLoading.collectAsState()
    val curriculumError by curriculumViewModel.error.collectAsState()


    val backStack = LocalBackStack.current
    val context = LocalContext.current

    // State for Add/Edit Todo Dialog
    var showTodoDialog by remember { mutableStateOf(false) }
    var editingTodoId by remember { mutableStateOf<String?>(null) }
    var todoInputValue by remember { mutableStateOf("") }
    var todoInputDesc by remember { mutableStateOf("") }
    var todoInputDue by remember { mutableStateOf("") }

    var showCompleted by remember { mutableStateOf(false) }

    val activeRoadmaps = curriculums.filter { (it.totalProgress ?: 0.0) < 100.0 }
    val completedRoadmaps = curriculums.filter { (it.totalProgress ?: 0.0) >= 100.0 }
    val roadmapsToShow = if (showCompleted) curriculums else activeRoadmaps

    // Sort todos: Incomplete first, Complete last
    val sortedTodos = todos.sortedBy { it.isCompleted }

    LaunchedEffect(Unit) {
        curriculumViewModel.loadCurriculums()
        viewModel.loadData()
    }

    LaunchedEffect(curriculumError) {
        curriculumError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            curriculumViewModel.clearError()
        }
    }

    Scaffold(
        bottomBar = {
            CustomBottomNavigation(
                activeTab = 2,
                onTabSelected = { index ->
                    when (index) {
                        0 -> backStack.add(Routes.DashboardRoute)
                        1 -> backStack.add(Routes.ExploreRoute)
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
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "My Learning Progress",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Black
                )
            }

            // LazyColumn for the rest of the content so everything is scrollable alongside ToDos
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // Section: AI Curriculums
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "My AI Roadmaps",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Black
                        )

                        if (completedRoadmaps.isNotEmpty()) {
                            Text(
                                text = if (showCompleted) "Hide Completed" else "View Completed (${completedRoadmaps.size})",
                                color = PrimaryBlue,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable { showCompleted = !showCompleted }
                                    .padding(4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (isLoadingCurriculums && curriculums.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = PrimaryBlue)
                        }
                    }
                } else if (roadmapsToShow.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, ComponentGray.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                                .background(WhitePure, RoundedCornerShape(16.dp))
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val emptyText = if (activeRoadmaps.isEmpty() && completedRoadmaps.isNotEmpty()) {
                                "All your roadmaps are completed! 🎉"
                            } else {
                                "No AI curriculums generated yet."
                            }
                            Text(
                                text = emptyText,
                                fontSize = 14.sp,
                                color = TextGray,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { backStack.add(Routes.ExploreRoute) },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("New Roadmap")
                                }

                                if (activeRoadmaps.isEmpty() && completedRoadmaps.isNotEmpty() && !showCompleted) {
                                    OutlinedButton(
                                        onClick = { showCompleted = true },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue),
                                        shape = RoundedCornerShape(8.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue)
                                    ) {
                                        Text("View Completed")
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                } else {
                    items(roadmapsToShow) { curriculum ->
                        CurriculumCard(
                            curriculum = curriculum,
                            onClick = {
                                backStack.add(Routes.CurriculumDetailRoute(curriculumId = curriculum.id))
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                
                item {
                    // To Do Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "To Do",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Black
                        )
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(BlackishBlue)
                                .clickable {
                                    editingTodoId = null
                                    todoInputValue = ""
                                    todoInputDesc = ""
                                    todoInputDue = ""
                                    showTodoDialog = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.plus_icon), 
                                contentDescription = "Add Todo", 
                                tint = WhitePure, 
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                items(sortedTodos) { todo ->
                    ProgressTodoItem(
                        todo = todo,
                        onClick = {
                            editingTodoId = todo.id
                            todoInputValue = todo.title
                            todoInputDesc = todo.description
                            todoInputDue = todo.dueDate
                            showTodoDialog = true
                        },
                        onToggle = { viewModel.toggleTodoCompleted(todo.id) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Detail / Add Dialog
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
fun CurriculumCard(
    curriculum: CurriculumApiModel,
    onClick: () -> Unit
) {
    val progressVal = curriculum.totalProgress ?: 0.0
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ComponentGray.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .background(WhitePure, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (progressVal >= 100.0) "Completed" else "In Progress",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Black
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Active path",
                fontSize = 14.sp,
                color = PrimaryBlue,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = curriculum.topic,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Black,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Progress", fontSize = 12.sp, color = PrimaryBlue)
            Text("${progressVal.toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Black)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Progress Bar Custom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFAAC4FF))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((progressVal / 100.0).coerceIn(0.0, 1.0).toFloat())
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(PrimaryBlue)
            )
        }
    }
}

@Composable
fun ProgressTodoItem(todo: Todo, onClick: () -> Unit, onToggle: () -> Unit) {
    val isCompleted = todo.isCompleted
    val opacity = if (isCompleted) 0.5f else 1f
    val bgColor = if (isCompleted) ComponentGray.copy(alpha = 0.2f) else WhitePure
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ComponentGray.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .background(bgColor, RoundedCornerShape(12.dp))
            .clickable { onClick() } // Open detail/edit modal
            .padding(
                vertical = if (isCompleted) 10.dp else 16.dp,
                horizontal = 16.dp
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween, 
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (!isCompleted && todo.dueDate.isNotEmpty()) {
                    Text(
                        text = todo.dueDate,
                        fontSize = 12.sp,
                        color = PrimaryBlue.copy(alpha = opacity)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                
                Text(
                    text = todo.title,
                    fontSize = if (isCompleted) 15.sp else 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Black.copy(alpha = opacity),
                    textDecoration = if (isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else androidx.compose.ui.text.style.TextDecoration.None
                )
                
                if (!isCompleted && todo.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = todo.description,
                        fontSize = 12.sp,
                        color = TextGray.copy(alpha = opacity),
                        lineHeight = 16.sp
                    )
                }
            }
            
            // Checkbox logic for toggle check state
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .border(2.dp, PrimaryBlue.copy(alpha = opacity), CircleShape)
                    .background(if (isCompleted) PrimaryBlue.copy(alpha = opacity) else Color.Transparent)
                    .clickable { onToggle() },
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(WhitePure.copy(alpha = opacity)))
                }
            }
        }
    }
}

