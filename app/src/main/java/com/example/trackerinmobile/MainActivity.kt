package com.example.trackerinmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.trackerinmobile.ui.screens.dashboard.DashboardScreen
import com.example.trackerinmobile.ui.screens.login.LoginScreen
import com.example.trackerinmobile.ui.screens.register.RegisterScreen
import com.example.trackerinmobile.ui.screens.splash.SplashScreen
import com.example.trackerinmobile.ui.theme.TrackerinMobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrackerinMobileTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    // Simple state-based navigation for now
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (currentScreen) {
            is Screen.Splash -> {
                SplashScreen(onNavigateToLogin = {
                    currentScreen = Screen.Login
                })
            }
            is Screen.Login -> {
                LoginScreen(
                    onNavigateToRegister = {
                        currentScreen = Screen.Register
                    },
                    onNavigateToDashboard = {
                        currentScreen = Screen.Dashboard
                    }
                )
            }
            is Screen.Register -> {
                RegisterScreen(onNavigateToLogin = {
                    currentScreen = Screen.Login
                })
            }
            is Screen.Dashboard -> {
                DashboardScreen()
            }
        }
    }
}

sealed class Screen {
    object Splash : Screen()
    object Login : Screen()
    object Register : Screen()
    object Dashboard : Screen()
}
