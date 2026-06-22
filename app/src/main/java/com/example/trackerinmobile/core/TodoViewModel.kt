package com.example.trackerinmobile.core
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.UUID
import java.time.Instant
import java.time.Duration
import com.example.trackerinmobile.data.network.ApiService
import com.example.trackerinmobile.data.local.TokenManager
import com.example.trackerinmobile.data.model.progress.TodoRequest
import com.example.trackerinmobile.data.model.progress.TodoApiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody

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

    private val _weeklyActivity = MutableStateFlow<Map<String, Float>>(emptyMap())
    val weeklyActivity: StateFlow<Map<String, Float>> = _weeklyActivity.asStateFlow()

    private val _currentStreak = MutableStateFlow(1)
    val currentStreak: StateFlow<Int> = _currentStreak.asStateFlow()

    private val _profileImage = MutableStateFlow<String?>(tokenManager.getProfileImage())
    val profileImage: StateFlow<String?> = _profileImage.asStateFlow()

    private val _userName = MutableStateFlow(tokenManager.getUserName() ?: "User")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow(tokenManager.getEmail() ?: "")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _dailyAverage = MutableStateFlow(0.0)
    val dailyAverage: StateFlow<Double> = _dailyAverage.asStateFlow()

    private val _completedRoadmapsCount = MutableStateFlow(0)
    val completedRoadmapsCount: StateFlow<Int> = _completedRoadmapsCount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedFilter = MutableStateFlow("Weekly")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private var monthlyHours = 0.0
    private var monthlyTopics = 0
    private var monthlyDays = 1
    private var monthlyAverage = 0.0

    private var weeklyHours = 0.0
    private var weeklyTopics = 0
    private var weeklyDays = 0
    private var weeklyAverage = 0.0

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
        updateDisplayStats()
    }

    private fun updateDisplayStats() {
        if (_selectedFilter.value == "Weekly") {
            _totalHours.value = weeklyHours
            _topicsCompleted.value = weeklyTopics
            _daysActive.value = weeklyDays
            _dailyAverage.value = weeklyAverage
        } else {
            _totalHours.value = monthlyHours
            _topicsCompleted.value = monthlyTopics
            _daysActive.value = monthlyDays
            _dailyAverage.value = monthlyAverage
        }
    }

    init {
        loadData()
    }
    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Fetch User profile to get dynamic stats
                val userResponse = apiService.getUserProfile()
                val user = userResponse.data

                // Cache user data locally
                tokenManager.saveUserName(user.name)
                tokenManager.saveEmail(user.email)
                tokenManager.saveProfileImage(user.profileImage)

                _userName.value = user.name
                _userEmail.value = user.email
                _profileImage.value = user.profileImage

                // Update SharedPreferences streak value
                user.currentStreak?.let {
                    tokenManager.saveCurrentStreak(it)
                }

                // Update dynamic statistics and activity map
                _currentStreak.value = user.currentStreak ?: 1
                val weeklyActivityMap = user.weeklyActivity ?: emptyMap()
                _weeklyActivity.value = weeklyActivityMap

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

                // Calculate dynamic elapsed session seconds
                val elapsedSeconds = parseLastLoginAt(user.lastLoginAt)
                val totalSeconds = (user.totalStudyTime ?: 0L) + elapsedSeconds
                val hours = totalSeconds / 3600.0

                // Calculate days active from the weekly activity log
                val activeDaysInWeek = weeklyActivityMap.filter { it.key != "year" && it.key != "week" && it.value >= 0f }.size
                val daysActiveValue = if (activeDaysInWeek > 0) activeDaysInWeek else 1
                val average = if (daysActiveValue > 0) hours / daysActiveValue else hours
                val completedCount = curriculums.count { (it.totalProgress ?: 0.0) >= 100.0 }

                // Monthly values (overall stats)
                monthlyHours = hours
                monthlyTopics = estimatedCompletedMilestones
                monthlyDays = tokenManager.getActiveDaysCount()
                monthlyAverage = if (monthlyDays > 0) hours / monthlyDays else hours

                // Weekly values (computed dynamically from the server weekly activity map)
                val weekdays = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
                var wHours = 0.0
                var wTopics = 0
                for (day in weekdays) {
                    val act = weeklyActivityMap[day] ?: -1f
                    if (act >= 0f) {
                        wHours += act * 0.05
                        if (act >= 40f) {
                            wTopics += (act / 40f).toInt()
                        }
                    }
                }
                // Add current session hours to today's weekday activity hours
                val todayStr = tokenManager.getTodayWeekdayString()
                val todayActivity = weeklyActivityMap[todayStr] ?: -1f
                if (todayActivity >= 0f && elapsedSeconds > 0L) {
                    wHours += elapsedSeconds / 3600.0
                }

                weeklyHours = wHours
                weeklyDays = daysActiveValue
                weeklyTopics = wTopics
                weeklyAverage = if (daysActiveValue > 0) wHours / daysActiveValue else 0.0

                _completedRoadmapsCount.value = completedCount
                updateDisplayStats()

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun parseLastLoginAt(lastLoginAt: String?): Long {
        if (lastLoginAt.isNullOrEmpty()) return 0L
        return try {
            val instant = Instant.parse(lastLoginAt)
            val diff = Duration.between(instant, Instant.now()).seconds
            if (diff > 0L) diff else 0L
        } catch (e: Exception) {
            try {
                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US)
                val date = sdf.parse(lastLoginAt)
                if (date != null) {
                    val diff = (System.currentTimeMillis() - date.time) / 1000L
                    if (diff > 0L) diff else 0L
                } else {
                    0L
                }
            } catch (ex: Exception) {
                0L
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

    fun uploadProfileImage(uri: android.net.Uri, context: android.content.Context) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val contentResolver = context.contentResolver
                val inputStream = contentResolver.openInputStream(uri) ?: return@launch
                val tempFile = java.io.File.createTempFile("avatar_", ".jpg", context.cacheDir)
                tempFile.deleteOnExit()
                tempFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }

                val requestFile = RequestBody.create(
                    "image/*".toMediaTypeOrNull(),
                    tempFile
                )
                val body = MultipartBody.Part.createFormData("profile_image", tempFile.name, requestFile)

                val response = apiService.uploadAvatar(body)
                
                // Cache new image URL
                tokenManager.saveProfileImage(response.profileImage)
                _profileImage.value = response.profileImage
                
                // Refresh data to keep stats/details updated
                loadData()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
