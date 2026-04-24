package com.example.trackerinmobile.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.trackerinmobile.R
import com.example.trackerinmobile.core.LocalBackStack
import com.example.trackerinmobile.core.Routes
import kotlinx.coroutines.delay

@Composable
fun SplashScreen() {
    val backStack = LocalBackStack.current

    LaunchedEffect(key1 = true) {
        delay(1000L) // Tunggu 1 detik
        backStack.clear()
        backStack.add(Routes.LoginRoute)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // Logo splash screen dengan ukuran yang telah disesuaikan (tidak terlalu besar)
        Image(
            painter = painterResource(id = R.drawable.trackerin_logo),
            contentDescription = "Trackerin Logo",
            modifier = Modifier.size(150.dp) // Ukuran dimodifikasi agar tidak terlalu besar
        )
    }
}
