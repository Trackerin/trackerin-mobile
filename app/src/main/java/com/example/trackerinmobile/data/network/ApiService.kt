package com.example.trackerinmobile.data.network

import com.example.trackerinmobile.data.model.auth.AuthResponse
import com.example.trackerinmobile.data.model.auth.GoogleAuthRequest
import com.example.trackerinmobile.data.model.auth.LoginRequest
import com.example.trackerinmobile.data.model.auth.RegisterRequest
import com.example.trackerinmobile.data.model.auth.SendOtpRequest
import com.example.trackerinmobile.data.model.auth.ResetPasswordRequest
import com.example.trackerinmobile.data.model.auth.MessageResponse
import com.example.trackerinmobile.data.model.progress.CurriculumsResponse
import com.example.trackerinmobile.data.model.progress.SingleTodoResponse
import com.example.trackerinmobile.data.model.progress.TodoRequest
import com.example.trackerinmobile.data.model.progress.TodoResponse
import com.example.trackerinmobile.data.model.progress.CurriculumDetailResponse
import com.example.trackerinmobile.data.model.progress.GenerateCurriculumRequest
import com.example.trackerinmobile.data.model.progress.GenerateCurriculumResponse
import com.example.trackerinmobile.data.model.progress.CompleteMilestoneRequest
import com.example.trackerinmobile.data.model.progress.CompleteMilestoneResponse
import com.example.trackerinmobile.data.model.progress.QuizResponse
import com.example.trackerinmobile.data.model.progress.ContactRequest
import com.example.trackerinmobile.data.model.note.NoteResponse
import com.example.trackerinmobile.data.model.note.SingleNoteResponse
import com.example.trackerinmobile.data.model.note.CreateNoteRequest
import com.example.trackerinmobile.data.model.note.UpdateNoteRequest
import com.example.trackerinmobile.data.model.notification.NotificationListResponse
import com.example.trackerinmobile.data.model.notification.NotificationItemResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("register/send-otp")
    suspend fun sendRegisterOtp(@Body request: SendOtpRequest): MessageResponse

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("login/google")
    suspend fun googleLogin(@Body request: GoogleAuthRequest): AuthResponse
    
    @POST("logout")
    suspend fun logout()

    @POST("forgot-password/send-otp")
    suspend fun sendForgotPasswordOtp(@Body request: SendOtpRequest): MessageResponse

    @POST("forgot-password/reset")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): MessageResponse

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

    // --- Notes Endpoints ---
    @GET("notes")
    suspend fun getNotes(): NoteResponse

    @POST("notes")
    suspend fun createNote(@Body request: CreateNoteRequest): SingleNoteResponse

    @PUT("notes/{id}")
    suspend fun updateNote(@Path("id") id: Int, @Body request: UpdateNoteRequest): SingleNoteResponse

    @DELETE("notes/{id}")
    suspend fun deleteNote(@Path("id") id: Int): MessageResponse

    // --- Curriculum & Milestones Endpoints ---
    @POST("curriculums/generate")
    suspend fun generateCurriculum(@Body request: GenerateCurriculumRequest): GenerateCurriculumResponse

    @GET("curriculums/{id}")
    suspend fun getCurriculumDetail(@Path("id") id: Int): CurriculumDetailResponse

    @DELETE("curriculums/{id}")
    suspend fun deleteCurriculum(@Path("id") id: Int): MessageResponse

    @PUT("milestones/{id}/complete")
    suspend fun completeMilestone(@Path("id") id: Int, @Body request: CompleteMilestoneRequest): CompleteMilestoneResponse

    @POST("milestones/{id}/generate-quiz")
    suspend fun generateQuiz(@Path("id") id: Int): QuizResponse

    // --- Contact Us Endpoint ---
    @POST("contact")
    suspend fun sendContactMessage(@Body request: ContactRequest): MessageResponse

    // --- Notifications Endpoints ---
    @GET("notifications")
    suspend fun getNotifications(): NotificationListResponse

    @PUT("notifications/{id}/read")
    suspend fun markNotificationAsRead(@Path("id") id: Int): NotificationItemResponse
}
