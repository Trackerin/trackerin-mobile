package com.example.trackerinmobile.core

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

data class Todo(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val isCompleted: Boolean = false
)

class TodoViewModel : ViewModel() {
    private val _todos = MutableStateFlow<List<Todo>>(
        listOf(
            Todo(title = "Do your homework"),
            Todo(title = "Finish UI Project"),
            Todo(title = "Review Computer Networks Task")
        )
    )
    val todos: StateFlow<List<Todo>> = _todos.asStateFlow()

    fun addTodo(title: String) {
        val current = _todos.value.toMutableList()
        current.add(Todo(title = title))
        _todos.value = current
    }

    fun toggleTodoCompleted(id: String) {
        val current = _todos.value.toMutableList()
        val index = current.indexOfFirst { it.id == id }
        if (index != -1) {
            val todo = current[index]
            current[index] = todo.copy(isCompleted = !todo.isCompleted)
            _todos.value = current
        }
    }

    fun updateTodo(id: String, newTitle: String) {
        val current = _todos.value.toMutableList()
        val index = current.indexOfFirst { it.id == id }
        if (index != -1) {
            val todo = current[index]
            current[index] = todo.copy(title = newTitle)
            _todos.value = current
        }
    }

    fun deleteTodo(id: String) {
        val current = _todos.value.toMutableList()
        current.removeAll { it.id == id }
        _todos.value = current
    }
}

