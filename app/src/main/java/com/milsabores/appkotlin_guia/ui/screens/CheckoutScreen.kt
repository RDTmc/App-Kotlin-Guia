package com.milsabores.appkotlin_guia.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.milsabores.appkotlin_guia.viewmodel.CartViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    cartVm: CartViewModel
) {
    val ui by cartVm.ui.collectAsState()

    // estados locales (podemos mandarlos al VM después)
    var address by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var payment by remember { mutableStateOf("transferencia") }
    var coupon by remember { mutableStateOf("") }

    // errores
    val addressError = address.isBlank()
    val dateError = date.isBlank()
    val timeError = time.isBlank()

    // descuentos UI
    var useFelices by remember { mutableStateOf(false) }
    var use50Anios by remember { mutableStateOf(false) }
    var useDuoc by remember { mutableStateOf(false) }

    // asegurar que no se activen ambos porcentuales
    LaunchedEffect(useFelices, use50Anios) {
        if (useFelices && use50Anios) {
            // si activó el de 50 años, desactivamos el de 10
            useFelices = false
        }
    }

    // cálculo local de descuentos
    val baseSubtotal = ui.subtotal
    val baseIva = ui.iva
    val baseShipping = ui.shipping

    val percentDiscount = when {
        use50Anios -> (baseSubtotal * 0.50).toInt()
        useFelices -> (baseSubtotal * 0.10).toInt()
        else -> 0
    }

    val shippingFinal = if (useDuoc) 0 else baseShipping
    val totalFinal = baseSubtotal - percentDiscount + baseIva + shippingFinal

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") }
            )
        }
    ) { pv ->
        Column(
            modifier = Modifier
                .padding(pv)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 1. Dirección de entrega
            Text("1. Dirección de entrega", fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Dirección (obligatoria)") },
                isError = addressError
            )
            if (addressError) {
                Text(
                    "Ingresa una dirección válida",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // 2. Fecha y hora
            Text("2. Fecha y hora de entrega", fontWeight = FontWeight.SemiBold)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Fecha (dd-mm)") },
                    isError = dateError
                )
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Hora (ej. 15:30)") },
                    isError = timeError
                )
            }
            if (dateError || timeError) {
                Text(
                    "Indica fecha y hora para la entrega",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // 3. Método de pago
            Text("3. Método de pago", fontWeight = FontWeight.SemiBold)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    RadioButton(
                        selected = payment == "webpay",
                        onClick = { payment = "webpay" }
                    )
                    Text("Tarjeta / Webpay / Mercado Pago")
                }
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    RadioButton(
                        selected = payment == "transferencia",
                        onClick = { payment = "transferencia" }
                    )
                    Text("Transferencia")
                }
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    RadioButton(
                        selected = payment == "efectivo",
                        onClick = { payment = "efectivo" }
                    )
                    Text("Efectivo al recibir")
                }
            }

            // 3.1 Descuentos (tu IL2.2)
            Text("Descuentos disponibles", fontWeight = FontWeight.SemiBold)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(
                    selected = useFelices,
                    onClick = { useFelices = !useFelices },
                    label = { Text("FELICES50 (10%)") },
                    enabled = !use50Anios // no dejar activar si ya está 50 años
                )
                FilterChip(
                    selected = use50Anios,
                    onClick = { use50Anios = !use50Anios },
                    label = { Text("50 años Mil Sabores (50%)") }
                )
                FilterChip(
                    selected = useDuoc,
                    onClick = { useDuoc = !useDuoc },
                    label = { Text("Duoc cumpleaños (envío gratis)") }
                )
            }

            // 4. Resumen del pedido
            Text("4. Resumen", fontWeight = FontWeight.SemiBold)
            if (ui.items.isEmpty()) {
                Text("No hay productos en el carrito.")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 140.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(ui.items) { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${item.quantity}× ${item.name}")
                            Text("$${item.lineTotal}")
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = coupon,
                    onValueChange = { coupon = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Cupón de descuento (opcional)") }
                )
                Spacer(Modifier.height(4.dp))

                Text("Subtotal: $${baseSubtotal}")
                Text("IVA (19%): $${baseIva}")
                Text("Envío: $${shippingFinal}")
                if (percentDiscount > 0) {
                    Text("Descuento aplicado: -$${percentDiscount}")
                }
                if (useDuoc) {
                    Text("Beneficio Duoc cumpleaños aplicado", color = MaterialTheme.colorScheme.primary)
                }
                Text(
                    "Total a pagar: $${totalFinal}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    // aquí iría el guardado
                    navController.navigateUp()
                },
                enabled = !addressError && !dateError && !timeError && ui.items.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar compra")
            }
        }
    }
}