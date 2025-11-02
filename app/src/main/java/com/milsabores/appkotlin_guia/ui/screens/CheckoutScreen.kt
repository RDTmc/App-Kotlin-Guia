package com.milsabores.appkotlin_guia.ui.screens

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.milsabores.appkotlin_guia.viewmodel.CartViewModel
import com.milsabores.appkotlin_guia.navigation.AppRoute
import com.milsabores.appkotlin_guia.viewmodel.CartUiState
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    cartVm: CartViewModel
) {
    val ui by cartVm.ui.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }

    // fecha
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    var fechaTexto by remember { mutableStateOf("") }

    // hora
    val horas = remember {
        (8..20).map { h -> "%02d:00".format(h) }
    }
    var hora by remember { mutableStateOf(horas.first()) }
    var horaExpanded by remember { mutableStateOf(false) }

    // pago
    var metodo by remember { mutableStateOf("Efectivo") }

    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Pago") }) }
    ) { pv ->
        Column(
            modifier = Modifier
                .padding(pv)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
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

                // Fecha (con diálogo)
                OutlinedTextField(
                    value = fechaTexto,
                    onValueChange = {},
                    label = { Text("Fecha de entrega") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                )

                // Hora (dropdown)
                ExposedDropdownMenuBox(
                    expanded = horaExpanded,
                    onExpandedChange = { horaExpanded = !horaExpanded }
                ) {
                    OutlinedTextField(
                        value = hora,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Hora de entrega") },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = horaExpanded,
                        onDismissRequest = { horaExpanded = false }
                    ) {
                        horas.forEach { h ->
                            DropdownMenuItem(
                                text = { Text(h) },
                                onClick = {
                                    hora = h
                                    horaExpanded = false
                                }
                            )
                        }
                    }
                }

                Text("Método de pago", style = MaterialTheme.typography.titleMedium)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    PaymentRadio("Efectivo", metodo) { metodo = it }
                    PaymentRadio("Transferencia", metodo) { metodo = it }
                    PaymentRadio("Tarjeta / Webpay", metodo) { metodo = it }
                }

                SummaryCheckout(ui = ui)

                if (error != null) {
                    Text(error!!, color = MaterialTheme.colorScheme.error)
                }
            }

            // botón fijo abajo
            Button(
                onClick = {
                    if (nombre.isBlank() || direccion.isBlank() || fechaTexto.isBlank() || hora.isBlank()) {
                        error = "Completa todos los campos."
                        return@Button
                    }
                    if (ui.items.isEmpty()) {
                        error = "Tu carrito está vacío."
                        return@Button
                    }

                    cartVm.clear()
                    navController.navigate(AppRoute.CheckoutSuccess.route) {
                        popUpTo(AppRoute.Checkout.route) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = androidx.compose.ui.graphics.Color(0xFF573123),
                    contentColor = androidx.compose.ui.graphics.Color.White
                )
            ) {
                Text("Confirmar compra")
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null && Build.VERSION.SDK_INT >= 26) {
                        val date = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        fechaTexto = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun PaymentRadio(
    label: String,
    selected: String,
    onSelected: (String) -> Unit
) {
    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        RadioButton(
            selected = selected == label,
            onClick = { onSelected(label) }
        )
        Text(label)
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
                Text("Subtotal"); Text(formatCLP(ui.subtotal))
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("IVA (19%)"); Text(formatCLP(ui.iva))
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Envío"); Text(formatCLP(ui.shipping))
            }
            Divider()
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total", fontWeight = FontWeight.Bold)
                Text(formatCLP(ui.total), fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun formatCLP(value: Int): String =
    "$" + "%,d".format(value).replace(',', '.')