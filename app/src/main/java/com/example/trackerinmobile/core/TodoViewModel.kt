package com.example.trackerinmobile.core
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.UUID
import com.example.trackerinmobile.data.network.ApiService
import com.example.trackerinmobile.data.local.TokenManager
import com.example.trackerinmobile.data.model.progress.TodoRequest
import com.example.trackerinmobile.data.model.progress.TodoApiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class Todo(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val dueDate: String = "",
    val isCompleted: Boolean = false
)

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val apiService: ApiService,
    val tokenManager: TokenManager
) : ViewModel() {
    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos.asStateFlow()
    private val _curriculumProgress = MutableStateFlow(0)
    val curriculumProgress: StateFlow<Int> = _curriculumProgress.asStateFlow()
    private val _curriculumTitle = MutableStateFlow("-")
    val curriculumTitle: StateFlow<String> = _curriculumTitle.asStateFlow()
    
    private val _curriculumId = MutableStateFlow<Int?>(null)
    val curriculumId: StateFlow<Int?> = _curriculumId.asStateFlow()

    private val _topicsCompleted = MutableStateFlow(0)
    val topicsCompleted: StateFlow<Int> = _topicsCompleted.asStateFlow()

    private val _totalHours = MutableStateFlow(0.0)
    val totalHours: StateFlow<Double> = _totalHours.asStateFlow()

    private val _daysActive = MutableStateFlow(1)
    val daysActive: StateFlow<Int> = _daysActive.asStateFlow()

    private val _dailyAverage = MutableStateFlow(0.0)
    val dailyAverage: StateFlow<Double> = _dailyAverage.asStateFlow()

    private val _completedRoadmapsCount = MutableStateFlow(0)
    val completedRoadmapsCount: StateFlow<Int> = _completedRoadmapsCount.asStateFlow()

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
                val curriculums = curriculumsResponse.data ?: emptyList()
                val activeCurriculum = curriculums.firstOrNull { (it.totalProgress ?: 0.0) < 100.0 }
                
                if (activeCurriculum != null) {
                    _curriculumId.value = activeCurriculum.id
                    _curriculumTitle.value = activeCurriculum.topic
                    _curriculumProgress.value = activeCurriculum.totalProgress?.toInt() ?: 0
                } else {
                    val latest = curriculums.firstOrNull()
                    if (latest != null) {
                        _curriculumId.value = latest.id
                        _curriculumTitle.value = latest.topic
                        _curriculumProgress.value = latest.totalProgress?.toInt() ?: 0
                    } else {
                        _curriculumId.value = null
                        _curriculumTitle.value = "No Active Path"
                        _curriculumProgress.value = 0
                    }
                }

                // Compute Stats
                val estimatedCompletedMilestones = curriculums.sumOf { 
                    (((it.totalProgress ?: 0.0) / 100.0) * 6.0).toInt() 
                }

                // Fetch Todos
                val todosResponse = apiService.getTodos()
                val parsedTodos = todosResponse.data?.map { parseTodo(it) } ?: emptyList()
                _todos.value = parsedTodos

                val completedTasks = parsedTodos.count { it.isCompleted }

                // Track active days and streak
                tokenManager.updateStreakAndActiveDays()

                val activeDays = tokenManager.getActiveDaysCount()
                val hours = (estimatedCompletedMilestones * 2.0) + (completedTasks * 0.5)
                val average = if (activeDays > 0) hours / activeDays else hours
                val completedCount = curriculums.count { (it.totalProgress ?: 0.0) >= 100.0 }

                _topicsCompleted.value = estimatedCompletedMilestones
                _totalHours.value = hours
                _daysActive.value = activeDays
                _dailyAverage.value = average
                _completedRoadmapsCount.value = completedCount

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
            if (newStatus) {
                tokenManager.incrementDailyActivity(20f)
            }
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
