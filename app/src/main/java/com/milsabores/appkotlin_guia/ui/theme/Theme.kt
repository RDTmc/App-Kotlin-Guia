package com.milsabores.appkotlin_guia.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val MilSaboresColorScheme = lightColorScheme(
    primary = Chocolate,
    onPrimary = Color.White,
    secondary = RosaSuave,
    onSecondary = Color.White,
    background = CremaPastel,
    onBackground = TextoPrincipal,
    surface = Color.White,
    onSurface = TextoPrincipal,
)

@Composable
fun AppKotlin_GuiaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MilSaboresColorScheme,
        typography = AppTypography,
        content = content
    )
}