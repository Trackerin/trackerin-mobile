package com.example.trackerinmobile.data.model.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    @SerialName("password_confirmation")
    val passwordConfirmation: String,
    val otp: String
)
// Tambahan field untuk menyesuaikan request di RegisterScreen (misal DOB, Status, dsb) bisa ditambahkan jika disupport backend.
// Namun api docs hanya menyebut name, email, password, password_confirmation.
// Untuk saat ini kita petakan saja name ke username atau fullName.

@Serializable
data class GoogleAuthRequest(
    val token: String
)

@Serializable
data class AuthResponse(
    val message: String? = null,
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("token_type")
    val tokenType: String? = null,
    val data: User
)

@Serializable
data class User(
    val id: Int,
    val name: String,
    val email: String,
    @SerialName("google_id")
    val googleId: String? = null,
    @SerialName("email_verified_at")
    val emailVerifiedAt: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    @SerialName("last_login_at")
    val lastLoginAt: String? = null,
    @SerialName("total_study_time")
    val totalStudyTime: Long? = null,
    @SerialName("current_streak")
    val currentStreak: Int? = null,
    @SerialName("last_active_date")
    val lastActiveDate: String? = null,
    @SerialName("weekly_activity")
    val weeklyActivity: Map<String, Float>? = null,
    @SerialName("profile_image")
    val profileImage: String? = null
)

@Serializable
data class UserResponse(
    val data: User
)

@Serializable
data class AvatarUploadResponse(
    val message: String? = null,
    @SerialName("profile_image")
    val profileImage: String
)


@Serializable
data class SendOtpRequest(
    val email: String
)

@Serializable
data class ResetPasswordRequest(
    val email: String,
    val otp: String,
    val password: String,
    @SerialName("password_confirmation")
    val passwordConfirmation: String
)

@Serializable
data class MessageResponse(
    val message: String
)

