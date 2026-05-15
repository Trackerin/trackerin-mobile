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
    val passwordConfirmation: String
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
    val updatedAt: String? = null
)
