package com.milsabores.appkotlin_guia.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.milsabores.appkotlin_guia.viewmodel.CartViewModel
import com.milsabores.appkotlin_guia.navigation.AppRoute
import com.milsabores.appkotlin_guia.viewmodel.CartUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    cartVm: CartViewModel
) {
    val ui by cartVm.ui.collectAsState()

    // estado local del formulario
    var nombre by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var metodo by remember { mutableStateOf("Efectivo") }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Checkout") })
        }
    ) { pv ->
        Column(
            modifier = Modifier
                .padding(pv)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Datos de entrega", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre completo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección de entrega") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = fecha,
                    onValueChange = { fecha = it },
                    label = { Text("Fecha (dd-mm)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = hora,
                    onValueChange = { hora = it },
                    label = { Text("Hora") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Text("Método de pago", style = MaterialTheme.typography.titleMedium)

            // radio simple
            PaymentOptionRow(
                selected = metodo,
                onSelected = { metodo = it }
            )

            // resumen rápido
            SummaryCheckout(ui = ui)

            if (error != null) {
                Text(error!!, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    // validación mínima
                    if (nombre.isBlank() || direccion.isBlank() || fecha.isBlank() || hora.isBlank()) {
                        error = "Completa todos los campos."
                        return@Button
                    }
                    if (ui.items.isEmpty()) {
                        error = "Tu carrito está vacío."
                        return@Button
                    }

                    // OK 👉 limpiamos carrito y vamos al resumen (o home)
                    cartVm.clear()
                    navController.navigate(AppRoute.CheckoutSuccess.route) {
                        popUpTo(AppRoute.Checkout.route) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = androidx.compose.ui.graphics.Color(0xFF573123),
                    contentColor = androidx.compose.ui.graphics.Color.White
                )
            ) {
                Text("Confirmar compra")
            }
        }
    }
}

@Composable
private fun PaymentOptionRow(
    selected: String,
    onSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            RadioButton(
                selected = selected == "Efectivo",
                onClick = { onSelected("Efectivo") }
            )
            Text("Efectivo al recibir")
        }
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            RadioButton(
                selected = selected == "Transferencia",
                onClick = { onSelected("Transferencia") }
            )
            Text("Transferencia")
        }
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            RadioButton(
                selected = selected == "Tarjeta",
                onClick = { onSelected("Tarjeta") }
            )
            Text("Tarjeta / Webpay")
        }
    }
}

@Composable
private fun SummaryCheckout(ui: CartUiState) {
    Surface(
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Subtotal")
                Text(formatCLP(ui.subtotal))
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("IVA (19%)")
                Text(formatCLP(ui.iva))
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Envío")
                Text(formatCLP(ui.shipping))
            }
            Divider()
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total", fontWeight = FontWeight.Bold)
                Text(formatCLP(ui.total), fontWeight = FontWeight.Bold)
            }
        }
    }
}

// lo reutilizamos acá también
private fun formatCLP(value: Int): String =
    "$" + "%,d".format(value).replace(',', '.')