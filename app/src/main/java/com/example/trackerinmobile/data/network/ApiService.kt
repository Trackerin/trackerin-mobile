package com.example.trackerinmobile.data.network

import com.example.trackerinmobile.data.model.auth.AuthResponse
import com.example.trackerinmobile.data.model.auth.GoogleAuthRequest
import com.example.trackerinmobile.data.model.auth.LoginRequest
import com.example.trackerinmobile.data.model.auth.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("login/google")
    suspend fun googleLogin(@Body request: GoogleAuthRequest): AuthResponse
    
    @POST("logout")
    suspend fun logout()
}
