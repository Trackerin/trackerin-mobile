package com.example.trackerinmobile.core

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
object Routes {
    @Serializable
    data object SplashRoute : NavKey

    @Serializable
    data object LoginRoute : NavKey

    @Serializable
    data object RegisterRoute : NavKey

    @Serializable
    data class RegisterOtpRoute(
        val name: String,
        val email: String,
        val password: String
    ) : NavKey

    @Serializable
    data object ForgotPasswordRoute : NavKey

    @Serializable
    data class ResetPasswordRoute(
        val email: String
    ) : NavKey

    @Serializable
    data object DashboardRoute : NavKey

    @Serializable
    data object ExploreRoute : NavKey

    @Serializable
    data object ProfileRoute : NavKey

    @Serializable
    data object ProgressRoute : NavKey
}
