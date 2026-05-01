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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trackerinmobile.R
import com.example.trackerinmobile.core.LocalBackStack
import com.example.trackerinmobile.core.Routes
import com.example.trackerinmobile.core.Todo
import com.example.trackerinmobile.core.TodoViewModel
import com.example.trackerinmobile.ui.components.CustomBottomNavigation
import com.example.trackerinmobile.ui.theme.*

@Composable
fun ProgressScreen(viewModel: TodoViewModel = viewModel()) {
    val todos by viewModel.todos.collectAsState()
    val backStack = LocalBackStack.current

    // State for Add/Edit Todo Dialog
    var showTodoDialog by remember { mutableStateOf(false) }
    var editingTodoId by remember { mutableStateOf<String?>(null) }
    var todoInputValue by remember { mutableStateOf("") }
    var todoInputDesc by remember { mutableStateOf("") }
    var todoInputDue by remember { mutableStateOf("") }

    // Sort todos: Incomplete first, Complete last
    val sortedTodos = todos.sortedBy { it.isCompleted }

    Scaffold(
        bottomBar = {
            CustomBottomNavigation(
                activeTab = 2,
                onTabSelected = { index ->
                    when (index) {
                        0 -> backStack.add(Routes.DashboardRoute)
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
                item {
                    RoadmapCard()
                    Spacer(modifier = Modifier.height(32.dp))
                    
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
fun RoadmapCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ComponentGray.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .background(WhitePure, RoundedCornerShape(16.dp))
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
                    text = "In Progress",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Black
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Update 2 days ago",
                fontSize = 14.sp,
                color = PrimaryBlue,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Curriculum",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Black,
            lineHeight = 36.sp
        )
        Text(
            text = "Roadmap",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Black,
            lineHeight = 36.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Progres view", fontSize = 12.sp, color = PrimaryBlue)
            Text("60%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Black)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Progress Bar Custom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFAAC4FF))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(PrimaryBlue)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { /* TODO Reset */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(1.dp, PrimaryBlue, RoundedCornerShape(8.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFC6D8FF),
                contentColor = Black
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Reset Roadmap", fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ProgressTodoItem(todo: Todo, onClick: () -> Unit, onToggle: () -> Unit) {
    val opacity = if (todo.isCompleted) 0.5f else 1f
    val bgColor = if (todo.isCompleted) ComponentGray.copy(alpha = 0.2f) else WhitePure
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ComponentGray.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .background(bgColor, RoundedCornerShape(12.dp))
            .clickable { onClick() } // Open detail/edit modal
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween, 
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (todo.dueDate.isNotEmpty()) {
                    Text(
                        text = todo.dueDate,
                        fontSize = 12.sp,
                        color = PrimaryBlue.copy(alpha = opacity)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                
                Text(
                    text = todo.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Black.copy(alpha = opacity)
                )
                
                if (todo.description.isNotEmpty()) {
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
                    .background(if (todo.isCompleted) PrimaryBlue.copy(alpha = opacity) else Color.Transparent)
                    .clickable { onToggle() },
                contentAlignment = Alignment.Center
            ) {
                if (todo.isCompleted) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(WhitePure.copy(alpha = opacity)))
                }
            }
        }
    }
}

