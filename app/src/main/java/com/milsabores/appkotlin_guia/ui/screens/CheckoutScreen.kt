package com.milsabores.appkotlin_guia.ui.screens

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.milsabores.appkotlin_guia.navigation.AppRoute
import com.milsabores.appkotlin_guia.ui.theme.BlancoDos
import com.milsabores.appkotlin_guia.ui.theme.Chocolate
import com.milsabores.appkotlin_guia.ui.theme.RosaClaro
import com.milsabores.appkotlin_guia.viewmodel.CartUiState
import com.milsabores.appkotlin_guia.viewmodel.CartViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    cartVm: CartViewModel,
    isGuest: Boolean = false
) {
    val ui by cartVm.ui.collectAsState()
    val scope = rememberCoroutineScope()

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

    // Modal para invitados
    var showLoginModal by remember { mutableStateOf(false) }

    // Estado de procesamiento y overlay
    var isProcessing by remember { mutableStateOf(false) }
    var showLoadingOverlay by remember { mutableStateOf(false) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Chocolate,
        unfocusedBorderColor = Chocolate.copy(alpha = 0.5f),
        focusedLabelColor = Chocolate,
        cursorColor = Chocolate,
        unfocusedContainerColor = BlancoDos,
        focusedContainerColor = BlancoDos
    )

    Scaffold(
        containerColor = BlancoDos,
        topBar = { TopAppBar(title = { Text("Pago") }) },
        bottomBar = {
            Button(
                onClick = {
                    // Si es invitado, primero pedir login/registro
                    if (isGuest) {
                        showLoginModal = true
                        return@Button
                    }

                    if (nombre.isBlank() || direccion.isBlank() || fechaTexto.isBlank() || hora.isBlank()) {
                        error = "Completa todos los campos."
                        return@Button
                    }
                    if (ui.items.isEmpty()) {
                        error = "Tu carrito está vacío."
                        return@Button
                    }

                    // Iniciar procesamiento
                    isProcessing = true
                    showLoadingOverlay = true

                    scope.launch {
                        // Simular delay de procesamiento
                        delay(4000)

                        // Limpiar carrito y navegar
                        cartVm.clear()
                        showLoadingOverlay = false
                        isProcessing = false

                        navController.navigate(AppRoute.CheckoutSuccess.route) {
                            popUpTo(AppRoute.Checkout.route) { inclusive = true }
                        }
                    }
                },
                enabled = !isProcessing,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Chocolate,
                    contentColor = RosaClaro
                )
            ) {
                Text(
                    if (isProcessing) "Procesando..." else "Confirmar compra",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    ) { pv ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .padding(pv)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 90.dp
                )
            ) {
                item { Text("Datos de entrega", style = MaterialTheme.typography.titleMedium) }

                item {
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre completo") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        enabled = !isProcessing
                    )
                }

                item {
                    OutlinedTextField(
                        value = direccion,
                        onValueChange = { direccion = it },
                        label = { Text("Dirección de entrega") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        enabled = !isProcessing
                    )
                }

                // Fila fecha + hora
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Fecha
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = fechaTexto,
                                onValueChange = {},
                                label = { Text("Fecha") },
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = textFieldColors,
                                enabled = !isProcessing
                            )
                            if (!isProcessing) {
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clickable { showDatePicker = true }
                                )
                            }
                        }

                        // Hora
                        Box(modifier = Modifier.weight(1f)) {
                            ExposedDropdownMenuBox(
                                expanded = horaExpanded && !isProcessing,
                                onExpandedChange = { if (!isProcessing) horaExpanded = !horaExpanded }
                            ) {
                                OutlinedTextField(
                                    value = hora,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Hora") },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth(),
                                    colors = textFieldColors,
                                    enabled = !isProcessing
                                )
                                ExposedDropdownMenu(
                                    expanded = horaExpanded && !isProcessing,
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

                item { Text("Método de pago", style = MaterialTheme.typography.titleMedium) }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                        PaymentRadio("Efectivo", metodo, !isProcessing) { metodo = it }
                        PaymentRadio("Transferencia", metodo, !isProcessing) { metodo = it }
                        PaymentRadio("Tarjeta / Webpay", metodo, !isProcessing) { metodo = it }
                    }
                }

                item { SummaryCheckout(ui) }

                if (error != null) {
                    item { Text(error!!, color = MaterialTheme.colorScheme.error) }
                }
            }

            // Loading Overlay
            if (showLoadingOverlay) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .clickable(enabled = false) { },
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.padding(32.dp),
                        colors = CardDefaults.cardColors(containerColor = BlancoDos),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(
                                color = Chocolate,
                                strokeWidth = 4.dp,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                "Estamos procesando tu pago...",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = Chocolate,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }

    // DatePicker
    if (showDatePicker && !isProcessing) {
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
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = Chocolate)
                ) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Modal login / registro para invitado
    if (showLoginModal) {
        AlertDialog(
            onDismissRequest = { showLoginModal = false },
            title = { Text("Inicia sesión") },
            text = { Text("Para finalizar tu compra debes iniciar sesión o crear tu cuenta.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLoginModal = false
                        navController.navigate(AppRoute.Login.route)
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Chocolate)
                ) {
                    Text("Iniciar sesión")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showLoginModal = false
                        navController.navigate(AppRoute.Register.route)
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Chocolate)
                ) {
                    Text("Registrarme")
                }
            }
        )
    }
}

@Composable
private fun PaymentRadio(
    label: String,
    selected: String,
    enabled: Boolean = true,
    onSelected: (String) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = selected == label,
            onClick = { if (enabled) onSelected(label) },
            enabled = enabled,
            colors = RadioButtonDefaults.colors(
                selectedColor = Chocolate,
                unselectedColor = Chocolate.copy(alpha = 0.7f)
            )
        )
        Text(
            label,
            color = if (enabled) Chocolate else Chocolate.copy(alpha = 0.5f)
        )
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

            HorizontalDivider(
                thickness = 1.dp,
                color = Chocolate.copy(alpha = 0.2f)
            )

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total", fontWeight = FontWeight.Bold)
                Text(formatCLP(ui.total), fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun formatCLP(value: Int): String =
    "$" + "%,d".format(value).replace(',', '.')