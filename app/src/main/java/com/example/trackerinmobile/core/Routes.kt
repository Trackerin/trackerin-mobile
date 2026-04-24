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
    data object DashboardRoute : NavKey
}

