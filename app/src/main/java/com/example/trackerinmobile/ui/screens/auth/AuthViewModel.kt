package com.example.trackerinmobile.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trackerinmobile.data.local.TokenManager
import com.example.trackerinmobile.data.model.auth.GoogleAuthRequest
import com.example.trackerinmobile.data.model.auth.LoginRequest
import com.example.trackerinmobile.data.model.auth.RegisterRequest
import com.example.trackerinmobile.data.model.auth.SendOtpRequest
import com.example.trackerinmobile.data.model.auth.ResetPasswordRequest
import com.example.trackerinmobile.data.model.progress.ContactRequest
import com.example.trackerinmobile.data.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import retrofit2.HttpException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    object RegisterOtpSent : AuthState()
    object ForgotPasswordOtpSent : AuthState()
    object PasswordResetSuccess : AuthState()
    object ContactSuccess : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val apiService: ApiService,
    val tokenManager: TokenManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

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
                401 -> "Invalid credentials"
                422 -> "Validation error"
                else -> "Server error (${e.code()})"
            }
        }
        return e.message ?: "An unexpected error occurred"
    }

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = apiService.login(request)
                tokenManager.saveToken(response.accessToken)
                tokenManager.saveUserName(response.data.name)
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(parseErrorBody(e))
            }
        }
    }

    fun register(request: RegisterRequest) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = apiService.register(request)
                tokenManager.saveToken(response.accessToken)
                tokenManager.saveUserName(response.data.name)
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(parseErrorBody(e))
            }
        }
    }

    fun sendRegisterOtp(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                apiService.sendRegisterOtp(SendOtpRequest(email))
                _authState.value = AuthState.RegisterOtpSent
            } catch (e: Exception) {
                _authState.value = AuthState.Error(parseErrorBody(e))
            }
        }
    }

    fun sendForgotPasswordOtp(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                apiService.sendForgotPasswordOtp(SendOtpRequest(email))
                _authState.value = AuthState.ForgotPasswordOtpSent
            } catch (e: Exception) {
                _authState.value = AuthState.Error(parseErrorBody(e))
            }
        }
    }

    fun resetPassword(request: ResetPasswordRequest) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                apiService.resetPassword(request)
                _authState.value = AuthState.PasswordResetSuccess
            } catch (e: Exception) {
                _authState.value = AuthState.Error(parseErrorBody(e))
            }
        }
    }

    fun googleLogin(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = apiService.googleLogin(GoogleAuthRequest(idToken))
                tokenManager.saveToken(response.accessToken)
                tokenManager.saveUserName(response.data.name)
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(parseErrorBody(e))
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                apiService.logout()
            } catch (e: Exception) {
                // Even if API fails, we clear local token
            } finally {
                tokenManager.clearToken()
            }
        }
    }

    fun sendContactMessage(name: String, email: String, subject: String, message: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                apiService.sendContactMessage(ContactRequest(name, email, subject, message))
                _authState.value = AuthState.ContactSuccess
            } catch (e: Exception) {
                _authState.value = AuthState.Error(parseErrorBody(e))
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

