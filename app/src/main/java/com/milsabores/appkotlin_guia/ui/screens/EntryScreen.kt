package com.milsabores.appkotlin_guia.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryScreen(
    onGuestClick: () -> Unit,
    onLoginClick: () -> Unit,
    onResetOnboardingClick: (() -> Unit)
) {
    Scaffold(topBar = { TopAppBar(title = { Text("¡Empecemos!") }) }) { pv ->
        Column(
            modifier = Modifier
                .padding(pv)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Explora como invitado o inicia sesión para comprar más rápido.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(24.dp))

            // Importante: llamar a la lambda dentro del onClick
            Button(onClick = { onGuestClick() }, modifier = Modifier.fillMaxWidth()) {
                Text("Entrar como Invitado")
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(onClick = { onLoginClick() }, modifier = Modifier.fillMaxWidth()) {
                Text("Iniciar sesión / Registrarme")
            }

            if (onResetOnboardingClick != null) {
                Spacer(Modifier.height(24.dp))
                TextButton(onClick = { onResetOnboardingClick() }) {
                    Text("Reiniciar Onboarding (debug)")
                }
            }

            Spacer(Modifier.height(24.dp))
            Text(
                "Como invitado podrás explorar el catálogo. Se solicitará tu login al comprar.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}