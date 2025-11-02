package com.milsabores.appkotlin_guia.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight


@Composable
fun EntryScreen(
    onGuestClick: () -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Explora como invitado o inicia sesión para comprar más rápido.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(28.dp))

        Button(
            onClick = onGuestClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
        ) {
            Text("Entrar como invitado", fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(14.dp))

        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
        ) {
            Text("Iniciar sesión", fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(14.dp))

        OutlinedButton(
            onClick = onRegisterClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Crear cuenta")
        }
    }
}