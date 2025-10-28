package com.milsabores.appkotlin_guia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onSkipClick: () -> Unit,
    onFinishClick: () -> Unit
) {
    var pageIndex by remember { mutableStateOf(0) }
    val slides: List<String> = listOf(
        "Descubre nuestras tortas del día",
        "Según la ocasión perfecta",
        "Personaliza sabor y tamaño"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("¡Bienvenido!") },
                actions = {
                    // Llamar a la lambda dentro del onClick
                    TextButton(onClick = { onSkipClick() }) { Text("Saltar") }
                }
            )
        }
    ) { pv ->
        Column(
            modifier = Modifier
                .padding(pv)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = slides[pageIndex],
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    enabled = pageIndex > 0,
                    onClick = { pageIndex = (pageIndex - 1).coerceAtLeast(0) }
                ) { Text("Atrás") }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    repeat(slides.size) { idx ->
                        val selected = idx == pageIndex
                        Box(
                            Modifier
                                .size(if (selected) 10.dp else 8.dp)
                                .background(
                                    color = if (selected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outline,
                                    shape = MaterialTheme.shapes.extraSmall
                                )
                        )
                    }
                }

                Button(
                    onClick = {
                        if (pageIndex < slides.lastIndex) {
                            pageIndex += 1
                        } else {
                            // Invocar explícitamente
                            onFinishClick()
                        }
                    }
                ) {
                    Text(if (pageIndex < slides.lastIndex) "Siguiente" else "Comenzar")
                }
            }
        }
    }
}