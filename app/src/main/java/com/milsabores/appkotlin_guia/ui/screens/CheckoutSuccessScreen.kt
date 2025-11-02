package com.milsabores.appkotlin_guia.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.milsabores.appkotlin_guia.navigation.AppRoute

@Composable
fun CheckoutSuccessScreen(navController: NavController) {
    Scaffold { pv ->
        Box(
            modifier = Modifier
                .padding(pv)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(92.dp)
                )
                Text("Compra realizada", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Te enviaremos el detalle a tu correo.")
                Button(
                    onClick = {
                        navController.navigate(AppRoute.Home.route) {
                            popUpTo(AppRoute.Home.route) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF573123),
                        contentColor = Color.White
                    )
                ) {
                    Text("Volver al inicio")
                }
            }
        }
    }
}


