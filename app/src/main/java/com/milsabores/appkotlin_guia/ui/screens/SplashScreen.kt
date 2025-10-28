package com.milsabores.appkotlin_guia.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.milsabores.appkotlin_guia.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinish: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(3500)
        onFinish()
    }
    Box(Modifier.fillMaxSize()) {
        // Background
        Image(
            painter = painterResource(R.drawable.imagen_principal),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Capa semitransparente opcional
        // Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.25f)))

        // Logo + título
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.logo_pasteleria),
                contentDescription = "Mil Sabores",
                modifier = Modifier
                    .size(120.dp)
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}