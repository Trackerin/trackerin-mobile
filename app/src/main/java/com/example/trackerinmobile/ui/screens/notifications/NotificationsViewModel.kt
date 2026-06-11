package com.example.trackerinmobile.ui.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trackerinmobile.data.model.notification.NotificationApiModel
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

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<NotificationApiModel>>(emptyList())
    val notifications: StateFlow<List<NotificationApiModel>> = _notifications.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchNotifications()
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

    fun fetchNotifications() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = apiService.getNotifications()
                _notifications.value = response.data ?: emptyList()
            } catch (e: Exception) {
                _error.value = parseErrorBody(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun markAsRead(id: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.markNotificationAsRead(id)
                val currentList = _notifications.value.toMutableList()
                val index = currentList.indexOfFirst { it.id == id }
                if (index != -1) {
                    currentList[index] = response.data
                    _notifications.value = currentList
                }
            } catch (e: Exception) {
                _error.value = parseErrorBody(e)
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
