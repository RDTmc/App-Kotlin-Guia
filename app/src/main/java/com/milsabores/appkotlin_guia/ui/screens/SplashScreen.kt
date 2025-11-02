package com.milsabores.appkotlin_guia.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.runtime.getValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.milsabores.appkotlin_guia.R
import kotlinx.coroutines.delay
@Composable
fun SplashScreen(onFinish: () -> Unit) {
    val pulse = rememberInfiniteTransition(label = "pulse")
    val scale by pulse.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scaleAnim"
    )

    LaunchedEffect(Unit) {
        delay(5000)   // 5 segundos REALES
        onFinish()
    }

    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.imagen_principal),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Image(
            painter = painterResource(R.drawable.logo_pasteleria),
            contentDescription = "Mil Sabores",
            modifier = Modifier
                .align(Alignment.Center)
                .size(180.dp)
                .scale(scale)
        )
    }
}
