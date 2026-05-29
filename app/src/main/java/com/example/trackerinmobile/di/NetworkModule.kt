package com.example.trackerinmobile.di

import android.content.Context
import com.example.trackerinmobile.data.local.TokenManager
import com.example.trackerinmobile.data.network.ApiClient
import com.example.trackerinmobile.data.network.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
        return TokenManager(context)
    }

    @Provides
    @Singleton
    fun provideApiService(tokenManager: TokenManager): ApiService {
        return ApiClient.createApiService(tokenManager)
    }
}
