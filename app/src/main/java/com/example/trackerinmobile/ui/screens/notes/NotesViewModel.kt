package com.example.trackerinmobile.ui.screens.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trackerinmobile.data.model.note.CreateNoteRequest
import com.example.trackerinmobile.data.model.note.NoteApiModel
import com.example.trackerinmobile.data.model.note.UpdateNoteRequest
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
class NotesViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _notes = MutableStateFlow<List<NoteApiModel>>(emptyList())
    val notes: StateFlow<List<NoteApiModel>> = _notes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchNotes()
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

    fun fetchNotes() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = apiService.getNotes()
                _notes.value = response.data ?: emptyList()
            } catch (e: Exception) {
                _error.value = parseErrorBody(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createNote(title: String, content: String, milestoneId: Int? = null, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = apiService.createNote(CreateNoteRequest(title, content, milestoneId))
                _notes.value = _notes.value + response.data
                onSuccess()
            } catch (e: Exception) {
                _error.value = parseErrorBody(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateNote(id: Int, title: String, content: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = apiService.updateNote(id, UpdateNoteRequest(title, content))
                val currentList = _notes.value.toMutableList()
                val index = currentList.indexOfFirst { it.id == id }
                if (index != -1) {
                    currentList[index] = response.data
                    _notes.value = currentList
                }
                onSuccess()
            } catch (e: Exception) {
                _error.value = parseErrorBody(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteNote(id: Int, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                apiService.deleteNote(id)
                _notes.value = _notes.value.filter { it.id != id }
                onSuccess()
            } catch (e: Exception) {
                _error.value = parseErrorBody(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
