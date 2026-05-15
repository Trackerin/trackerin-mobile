package com.example.trackerinmobile

import android.app.Application
import com.example.trackerinmobile.di.AppContainer

class TrackerinApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

