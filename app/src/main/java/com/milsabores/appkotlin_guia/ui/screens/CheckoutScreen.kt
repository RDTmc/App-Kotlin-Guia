package com.milsabores.appkotlin_guia.ui.screens

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.milsabores.appkotlin_guia.navigation.AppRoute
import com.milsabores.appkotlin_guia.viewmodel.CartUiState
import com.milsabores.appkotlin_guia.viewmodel.CartViewModel
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
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var fechaTexto by remember { mutableStateOf("") }

    // horas 08:00..20:00
    val horas = remember { (8..20).map { "%02d:00".format(it) } }
    var hora by remember { mutableStateOf(horas.first()) }
    var horaExpanded by remember { mutableStateOf(false) }

    var metodo by remember { mutableStateOf("Efectivo") }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Pago") }) },
        bottomBar = {
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
    ) { pv ->
        LazyColumn(
            modifier = Modifier
                .padding(pv)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp)
        ) {
            item {
                Text("Datos de entrega", style = MaterialTheme.typography.titleMedium)
            }
            item {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre completo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección de entrega") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            // fila fecha + hora
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // fecha (con overlay clickeable)
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = fechaTexto,
                            onValueChange = {},
                            label = { Text("Fecha") },
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showDatePicker = true }
                        )
                    }

                    // hora
                    Box(modifier = Modifier.weight(1f)) {
                        ExposedDropdownMenuBox(
                            expanded = horaExpanded,
                            onExpandedChange = { horaExpanded = !horaExpanded }
                        ) {
                            OutlinedTextField(
                                value = hora,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Hora") },
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
                    }
                }
            }
            item {
                Text("Método de pago", style = MaterialTheme.typography.titleMedium)
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    PaymentRadio("Efectivo", metodo) { metodo = it }
                    PaymentRadio("Transferencia", metodo) { metodo = it }
                    PaymentRadio("Tarjeta / Webpay", metodo) { metodo = it }
                }
            }
            item {
                SummaryCheckout(ui)
            }
            if (error != null) {
                item {
                    Text(error!!, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }

    // diálogo de fecha
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
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun PaymentRadio(label: String, selected: String, onSelected: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = selected == label,
            onClick = { onSelected(label) }
        )
        Text(label)
    }
}

@Composable
private fun SummaryCheckout(ui: CartUiState) {
    Surface(tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
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
