package com.example.trackerinmobile.core

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

data class Todo(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val dueDate: String = "",
    val isCompleted: Boolean = false
)

class TodoViewModel : ViewModel() {
    private val _todos = MutableStateFlow<List<Todo>>(
        listOf(
            Todo(
                title = "Konfigurasi Database",
                description = "Melakukan konfigurasi Database menggunakan PHP agar bisa terhubung ke microsoft azure",
                dueDate = "Deadline 6/7/2026"
            ),
            Todo(
                title = "Membuat Fungsi Otomasisasi",
                description = "Buat fungsi di php untuk otomasisasi input dan output data dari database ke program.",
                dueDate = "Deadline 7/7/2026"
            ),
            Todo(
                title = "Membuat Tampilan Data",
                description = "Buat tampilan untuk menampilkan data dari database agar bisa dilihat oleh user",
                dueDate = "Deadline 10/7/2026"
            )
        )
    )
    val todos: StateFlow<List<Todo>> = _todos.asStateFlow()

    fun addTodo(title: String, description: String = "", dueDate: String = "") {
        val current = _todos.value.toMutableList()
        current.add(Todo(title = title, description = description, dueDate = dueDate))
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

    fun updateTodo(id: String, newTitle: String, newDesc: String = "", newDueDate: String = "") {
        val current = _todos.value.toMutableList()
        val index = current.indexOfFirst { it.id == id }
        if (index != -1) {
            val todo = current[index]
            current[index] = todo.copy(title = newTitle, description = newDesc, dueDate = newDueDate)
            _todos.value = current
        }
    }

    fun deleteTodo(id: String) {
        val current = _todos.value.toMutableList()
        current.removeAll { it.id == id }
        _todos.value = current
    }
}

