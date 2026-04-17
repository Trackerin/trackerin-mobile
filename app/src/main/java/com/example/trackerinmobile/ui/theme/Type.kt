package com.example.trackerinmobile.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.Font
import com.example.trackerinmobile.R

// Set of Material typography styles to start with
val GeistFontFamily = FontFamily(
    Font(R.font.geist_regular),
    Font(R.font.geist_semibold, FontWeight.SemiBold),
    Font(R.font.geist_bold, FontWeight.Bold)
)
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default, // Ganti ini dengan GeistFontFamily setelah menambahkan file font
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = (-0.04).em
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default, // Ganti dengan GeistFontFamily
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.04).em
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default, // Ganti dengan GeistFontFamily
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = (-0.04).em
    )
)