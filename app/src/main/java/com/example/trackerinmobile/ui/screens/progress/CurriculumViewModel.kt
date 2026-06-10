package com.example.trackerinmobile.ui.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trackerinmobile.data.model.progress.CurriculumApiModel
import com.example.trackerinmobile.data.model.progress.CurriculumDetailApiModel
import com.example.trackerinmobile.data.model.progress.GenerateCurriculumRequest
import com.example.trackerinmobile.data.model.progress.CompleteMilestoneRequest
import com.example.trackerinmobile.data.model.progress.QuizApiModel
import com.example.trackerinmobile.data.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import retrofit2.HttpException
import javax.inject.Inject

import com.example.trackerinmobile.data.local.TokenManager

@HiltViewModel
class CurriculumViewModel @Inject constructor(
    private val apiService: ApiService,
    val tokenManager: TokenManager
) : ViewModel() {

    fun setQuizCompleted(milestoneId: Int, isCompleted: Boolean) {
        tokenManager.setQuizCompleted(milestoneId, isCompleted)
    }

    fun isQuizCompleted(milestoneId: Int): Boolean {
        return tokenManager.isQuizCompleted(milestoneId)
    }

    private val _curriculums = MutableStateFlow<List<CurriculumApiModel>>(emptyList())
    val curriculums: StateFlow<List<CurriculumApiModel>> = _curriculums.asStateFlow()

    private val _curriculumDetail = MutableStateFlow<CurriculumDetailApiModel?>(null)
    val curriculumDetail: StateFlow<CurriculumDetailApiModel?> = _curriculumDetail.asStateFlow()

    private val _quiz = MutableStateFlow<QuizApiModel?>(null)
    val quiz: StateFlow<QuizApiModel?> = _quiz.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadCurriculums()
    }

    private fun parseErrorBody(e: Exception): String {
        if (e is HttpException) {
            try {
                val errorBodyString = e.response()?.errorBody()?.string()
                if (!errorBodyString.isNullOrEmpty()) {
                    val json = Json.parseToJsonElement(errorBodyString)
                    val serverMessage = json.jsonObject["message"]?.jsonPrimitive?.content
                    if (!serverMessage.isNullOrEmpty()) {
                        return serverMessage
                    }
                }
            } catch (parseException: Exception) {
                parseException.printStackTrace()
            }
            return when (e.code()) {
                422 -> "Validation error"
                else -> "Server error (${e.code()})"
            }
        }
        return e.message ?: "An unexpected error occurred"
    }

    fun loadCurriculums() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = apiService.getCurriculums()
                _curriculums.value = response.data ?: emptyList()
            } catch (e: Exception) {
                _error.value = parseErrorBody(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun generateCurriculum(topic: String, onSuccess: (Int) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = apiService.generateCurriculum(GenerateCurriculumRequest(topic))
                _curriculums.value = _curriculums.value + CurriculumApiModel(
                    id = response.data.id,
                    topic = response.data.topic,
                    totalProgress = response.data.totalProgress
                )
                onSuccess(response.data.id)
            } catch (e: Exception) {
                _error.value = parseErrorBody(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadCurriculumDetail(curriculumId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = apiService.getCurriculumDetail(curriculumId)
                _curriculumDetail.value = response.data
            } catch (e: Exception) {
                _error.value = parseErrorBody(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleMilestoneComplete(milestoneId: Int, isCompleted: Boolean) {
        viewModelScope.launch {
            _error.value = null
            try {
                val response = apiService.completeMilestone(milestoneId, CompleteMilestoneRequest(isCompleted))
                if (isCompleted) {
                    tokenManager.incrementDailyActivity(40f)
                }
                
                // Update local detail progress
                val currentDetail = _curriculumDetail.value
                if (currentDetail != null) {
                    val updatedMilestones = currentDetail.milestones.map {
                        if (it.id == milestoneId) response.data else it
                    }
                    
                    // Recalculate dynamic progress
                    val completedCount = updatedMilestones.count { it.isCompleted }
                    val newProgress = if (updatedMilestones.isNotEmpty()) {
                        (completedCount.toDouble() / updatedMilestones.size.toDouble()) * 100.0
                    } else {
                        0.0
                    }

                    _curriculumDetail.value = currentDetail.copy(
                        milestones = updatedMilestones,
                        totalProgress = newProgress
                    )
                }
            } catch (e: Exception) {
                _error.value = parseErrorBody(e)
            }
        }
    }

    fun generateQuiz(milestoneId: Int, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = apiService.generateQuiz(milestoneId)
                _quiz.value = response.data
                onSuccess()
            } catch (e: Exception) {
                _error.value = parseErrorBody(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearQuiz() {
        _quiz.value = null
    }

    fun clearError() {
        _error.value = null
    }
}
