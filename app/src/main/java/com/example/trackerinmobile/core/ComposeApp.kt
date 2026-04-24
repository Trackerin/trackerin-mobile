package com.example.trackerinmobile.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.trackerinmobile.ui.theme.TrackerinMobileTheme
import com.example.trackerinmobile.ui.screens.splash.SplashScreen
import com.example.trackerinmobile.ui.screens.login.LoginScreen
import com.example.trackerinmobile.ui.screens.register.RegisterScreen
import com.example.trackerinmobile.ui.screens.dashboard.DashboardScreen

@Composable
fun ComposeApp() {
    val backStack = rememberNavBackStack(Routes.SplashRoute)

    CompositionLocalProvider(LocalBackStack provides backStack) {
        TrackerinMobileTheme {
            NavDisplay(
                backStack = backStack,
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),
                entryProvider = entryProvider {
                    entry<Routes.SplashRoute> { SplashScreen() }
                    entry<Routes.LoginRoute> { LoginScreen() }
                    entry<Routes.RegisterRoute> { RegisterScreen() }
                    entry<Routes.DashboardRoute> { DashboardScreen() }
                }
            )
        }
    }
}

