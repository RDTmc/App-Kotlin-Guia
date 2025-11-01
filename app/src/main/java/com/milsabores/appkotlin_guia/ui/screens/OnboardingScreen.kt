package com.milsabores.appkotlin_guia.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.milsabores.appkotlin_guia.R
import kotlinx.coroutines.launch

data class OnbSlide(
    val title: String,
    val subtitle: String,
    val imageRes: Int
)

@Composable
fun OnboardingScreen(
    onFinishClick: () -> Unit,
    onSkipClick: () -> Unit
) {
    val slides = listOf(
        OnbSlide("Descubre nuestras tortas del día", "Frescura y sabor diario", R.drawable.onb_1),
        OnbSlide("Según la ocasión perfecta", "Cumpleaños, bodas, eventos", R.drawable.onb_2),
        OnbSlide("Conoce nuestros productos", "Tradicional, sin azúcar y vegano", R.drawable.onb_3),
    )
    val pagerState = rememberPagerState(pageCount = { slides.size })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val s = slides[page]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(Modifier.height(8.dp))
                Image(
                    painter = painterResource(id = s.imageRes),
                    contentDescription = s.title,
                    modifier = Modifier
                        .size(260.dp)
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(s.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    Text(s.subtitle, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                }
                Button(
                    onClick = {
                        if (page < slides.lastIndex) {
                            scope.launch { pagerState.animateScrollToPage(page + 1) }
                        } else {
                            onFinishClick()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                ) {
                    Text("¡Ver!")
                }
                OutlinedButton(
                    onClick = onSkipClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Saltar")
                }
            }
        }
    }
}