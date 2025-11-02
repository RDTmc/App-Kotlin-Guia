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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.milsabores.appkotlin_guia.R
import kotlinx.coroutines.delay
@Composable
fun SplashScreen(onFinish: () -> Unit) {
    val pulse = rememberInfiniteTransition(label = "pulse")
    val scale by pulse.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scaleAnim"
    )

    LaunchedEffect(Unit) {
        delay(5000)   // 5 segundos REALES
        onFinish()
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.imagen_principal),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        val screenHeight = this.maxHeight  // Altura del Box/Pantalla
        val imageHeight = 200.dp

        // Calcular el offset vertical (3/4 de la altura)
        // Se resta la mitad de la altura de la imagen para centrarla verticalmente en ese punto
        val topOffsetDp = screenHeight / 6f - imageHeight / 2f
        Image(
            painter = painterResource(R.drawable.logo_ms_pasteleria),
            contentDescription = "Mil Sabores",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = topOffsetDp)
                .size(imageHeight)
                .scale(scale)
        )
    }
}
