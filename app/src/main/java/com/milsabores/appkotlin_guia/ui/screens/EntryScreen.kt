package com.milsabores.appkotlin_guia.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.milsabores.appkotlin_guia.ui.theme.Cafe
import com.milsabores.appkotlin_guia.ui.theme.CafeOsc
import com.milsabores.appkotlin_guia.ui.theme.CafeText
import com.milsabores.appkotlin_guia.ui.theme.Rosa
import com.milsabores.appkotlin_guia.ui.theme.RosaText
import com.milsabores.appkotlin_guia.ui.theme.Vainilla

@Composable
fun EntryScreen(
    isLogged: Boolean,
    onGuestClick: () -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    if (isLogged) {
        // Si el usuario tiene sesión → se envia a Home
        LaunchedEffect(Unit) { onGuestClick() }
        return
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFAFAFA)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                buildAnnotatedString {
                    append("Ingresa como invitado o ")
                    withStyle(style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    ) {
                        append("inicia sesión para más ventajas.")
                    }
                },
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 32.dp),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(28.dp))

            ElevatedButton(
                onClick = onGuestClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = Cafe,
                    contentColor = CafeText
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Filled.PersonPin, contentDescription = "Invitado", modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text("Invitado", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }

            Spacer(Modifier.height(16.dp))

            FilledTonalButton(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Rosa,
                    contentColor = RosaText
                ),
                shape = MaterialTheme.shapes.medium

            ) {
                Icon(Icons.Filled.Person, contentDescription = "Iniciar sesión", modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text("Iniciar sesión", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onRegisterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Vainilla,
                    contentColor = CafeOsc
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Filled.AppRegistration, contentDescription = "Crear cuenta", modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text("Crear cuenta", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }

        }
    }
}