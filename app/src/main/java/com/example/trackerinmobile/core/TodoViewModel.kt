package com.example.trackerinmobile.core
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.UUID
import com.example.trackerinmobile.data.network.ApiService
import com.example.trackerinmobile.data.model.progress.TodoRequest
import com.example.trackerinmobile.data.model.progress.TodoApiModel
data class Todo(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val dueDate: String = "",
    val isCompleted: Boolean = false
)
class TodoViewModel(private val apiService: ApiService) : ViewModel() {
    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos.asStateFlow()
    private val _curriculumProgress = MutableStateFlow(0)
    val curriculumProgress: StateFlow<Int> = _curriculumProgress.asStateFlow()
    private val _curriculumTitle = MutableStateFlow("-")
    val curriculumTitle: StateFlow<String> = _curriculumTitle.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    init {
        loadData()
    }
    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Fetch Curriculums
                val curriculumsResponse = apiService.getCurriculums()
                val activeCurriculum = curriculumsResponse.data?.firstOrNull()
                if (activeCurriculum != null) {
                    _curriculumTitle.value = activeCurriculum.topic
                    _curriculumProgress.value = activeCurriculum.totalProgress ?: 0
                }
                // Fetch Todos
                val todosResponse = apiService.getTodos()
                val parsedTodos = todosResponse.data?.map { parseTodo(it) } ?: emptyList()
                _todos.value = parsedTodos
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
    private fun parseTodo(apiModel: TodoApiModel): Todo {
        return try {
            val json = JSONObject(apiModel.task)
            Todo(
                id = apiModel.id.toString(),
                title = json.optString("title", "Untitled"),
                description = json.optString("desc", ""),
                dueDate = json.optString("due", ""),
                isCompleted = apiModel.isDone
            )
        } catch (e: Exception) { // fallback if plain text
            Todo(
                id = apiModel.id.toString(),
                title = apiModel.task,
                isCompleted = apiModel.isDone
            )
        }
    }
    private fun formatTask(title: String, desc: String, due: String): String {
        val json = JSONObject()
        json.put("title", title)
        json.put("desc", desc)
        json.put("due", due)
        return json.toString()
    }
    fun addTodo(title: String, description: String = "", dueDate: String = "") {
        viewModelScope.launch {
            try {
                val taskString = formatTask(title, description, dueDate)
                val response = apiService.createTodo(TodoRequest(task = taskString, isDone = false))
                // Refresh list or add locally
                _todos.value = _todos.value + parseTodo(response.data)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun toggleTodoCompleted(id: String) {
        val current = _todos.value.toMutableList()
        val index = current.indexOfFirst { it.id == id }
        if (index != -1) {
            val todo = current[index]
            val newStatus = !todo.isCompleted
            // Optimistic update
            current[index] = todo.copy(isCompleted = newStatus)
            _todos.value = current
            viewModelScope.launch {
                try {
                    val taskString = formatTask(todo.title, todo.description, todo.dueDate)
                    apiService.updateTodo(id.toInt(), TodoRequest(task = taskString, isDone = newStatus))
                } catch (e: Exception) {
                    // Revert on failure
                    loadData()
                }
            }
        }
    }
    fun updateTodo(id: String, newTitle: String, newDesc: String = "", newDueDate: String = "") {
        val current = _todos.value.toMutableList()
        val index = current.indexOfFirst { it.id == id }
        if (index != -1) {
            val todo = current[index]
            // Optimistic update
            current[index] = todo.copy(title = newTitle, description = newDesc, dueDate = newDueDate)
            _todos.value = current
            viewModelScope.launch {
                try {
                    val taskString = formatTask(newTitle, newDesc, newDueDate)
                    apiService.updateTodo(id.toInt(), TodoRequest(task = taskString, isDone = todo.isCompleted))
                } catch (e: Exception) {
                    loadData()
                }
            }
        }
    }
    fun deleteTodo(id: String) {
        val current = _todos.value.toMutableList()
        current.removeAll { it.id == id }
        _todos.value = current
        viewModelScope.launch {
            try {
                apiService.deleteTodo(id.toInt())
            } catch (e: Exception) {
                loadData()
            }
        }
    }
}
class TodoViewModelFactory(
    private val apiService: ApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
