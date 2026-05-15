package com.example.trackerinmobile.data.network

import com.example.trackerinmobile.data.model.auth.AuthResponse
import com.example.trackerinmobile.data.model.auth.GoogleAuthRequest
import com.example.trackerinmobile.data.model.auth.LoginRequest
import com.example.trackerinmobile.data.model.auth.RegisterRequest
import com.example.trackerinmobile.data.model.progress.CurriculumsResponse
import com.example.trackerinmobile.data.model.progress.SingleTodoResponse
import com.example.trackerinmobile.data.model.progress.TodoRequest
import com.example.trackerinmobile.data.model.progress.TodoResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("login/google")
    suspend fun googleLogin(@Body request: GoogleAuthRequest): AuthResponse
    
    @POST("logout")
    suspend fun logout()

    // --- Progress & Todos Endpoints ---
    @GET("todos")
    suspend fun getTodos(): TodoResponse

    @POST("todos")
    suspend fun createTodo(@Body request: TodoRequest): SingleTodoResponse

    @PUT("todos/{id}")
    suspend fun updateTodo(@Path("id") id: Int, @Body request: TodoRequest): SingleTodoResponse

    @DELETE("todos/{id}")
    suspend fun deleteTodo(@Path("id") id: Int)

    @GET("curriculums")
    suspend fun getCurriculums(): CurriculumsResponse
}
