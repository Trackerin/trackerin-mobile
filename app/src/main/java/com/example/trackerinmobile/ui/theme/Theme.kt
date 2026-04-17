package com.example.trackerinmobile.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = WhitePure,
    primaryContainer = PrimaryBlueHover,
    secondary = ComponentGray,
    onSecondary = WhitePure,
    background = BackgroundApp,
    onBackground = Black,
    surface = WhitePure,
    onSurface = BlackishBlue,
    outline = TextGray
    /* Other defaults you could override:
    error = Color.Red,
    etc.
    */
)

@Composable
fun TrackerinMobileTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}