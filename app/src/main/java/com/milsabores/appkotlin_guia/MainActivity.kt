package com.milsabores.appkotlin_guia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.milsabores.appkotlin_guia.ui.HomeScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 👇 Ahora se muestra tu pantalla
            HomeScreen()
        }
    }
}