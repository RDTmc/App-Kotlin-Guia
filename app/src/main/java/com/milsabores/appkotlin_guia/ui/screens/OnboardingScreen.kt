package com.milsabores.appkotlin_guia.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.milsabores.appkotlin_guia.R
import kotlinx.coroutines.launch



@Composable
fun OnboardingScreen(
    onSkipClick: () -> Unit,
    onFinishClick: () -> Unit
) {
    val slides = listOf(
        OnbSlide(
            title = "Descubre nuestras tortas del día",
            subtitle = "Frescas, decoradas y listas",
            imageRes = R.drawable.onb_1
        ),
        OnbSlide(
            title = "Según la ocasión perfecta",
            subtitle = "Cumpleaños, bodas y más",
            imageRes = R.drawable.onb_2
        ),
        OnbSlide(
            title = "Conoce nuestros productos",
            subtitle = "Postres, veganos y sin azúcar",
            imageRes = R.drawable.onb_3
        )
    )

    val pagerState = rememberPagerState(pageCount = { slides.size })
    val currentPage = pagerState.currentPage
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        // 1) HorizontalPager solo con las imágenes
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val slide = slides[page]

            Image(
                painter = painterResource(id = slide.imageRes),
                contentDescription = slide.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // 2) Scrim gradient FUERA del pager (encima de todo)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x99000000),
                            Color(0xCC000000)
                        ),
                        startY = 300f
                    )
                )
        )

        // 3) Contenido de texto y botón FUERA del pager
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 40.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val slide = slides[currentPage]

            Text(
                text = slide.title,
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(4f, 4f),
                        blurRadius = 8f
                    )
                ),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = slide.subtitle,
                color = Color.White.copy(alpha = 0.95f),
                style = MaterialTheme.typography.bodyMedium.copy(
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(2f, 2f),
                        blurRadius = 6f
                    )
                ),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            OutlinedButton(
                onClick = onFinishClick,
                modifier = Modifier
                    .width(160.dp)
                    .height(48.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = MaterialTheme.shapes.medium
                    ),
                border = BorderStroke(2.dp, Color.White),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Black.copy(alpha = 0.4f),
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    "¡Ver!",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(Modifier.height(16.dp))
        }

        // 4) Icono "saltar" en la esquina superior derecha
        IconButton(
            onClick = {
                val last = pagerState.currentPage == slides.lastIndex
                if (last) {
                    onFinishClick()
                } else {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Saltar",
                tint = Color.White
            )
        }
    }
}

private data class OnbSlide(
    val title: String,
    val subtitle: String,
    val imageRes: Int
)