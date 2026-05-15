package com.example.trackerinmobile.di

import android.content.Context
import com.example.trackerinmobile.data.local.TokenManager
import com.example.trackerinmobile.data.network.ApiClient
import com.example.trackerinmobile.data.network.ApiService

/**
 * Dependency container for Manual DI
 */
class AppContainer(private val context: Context) {
    val tokenManager: TokenManager by lazy {
        TokenManager(context)
    }

    val apiService: ApiService by lazy {
        ApiClient.createApiService(tokenManager)
    }
}

