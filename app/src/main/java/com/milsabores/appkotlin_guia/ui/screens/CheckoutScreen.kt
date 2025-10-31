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
                isError = address.isBlank()
            )

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
                    label = { Text("Fecha (dd-mm)") }
                )
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Hora (ej. 15:30)") }
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

            // 4. Resumen del pedido
            Text("4. Resumen", fontWeight = FontWeight.SemiBold)
            if (ui.items.isEmpty()) {
                Text("No hay productos en el carrito.")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 160.dp),
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
                Text("Subtotal: $${ui.subtotal}")
                Text("IVA (19%): $${ui.iva}")
                Text("Envío: $${ui.shipping}")
                if (ui.discount != 0) {
                    Text("Descuento: -$${ui.discount}")
                }
                Text(
                    "Total: $${ui.total}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    // validaciones mínimas
                    if (address.isBlank()) return@Button
                    // aquí iría guardado en Room/Backend
                    navController.navigateUp()
                },
                enabled = address.isNotBlank() && ui.items.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar compra")
            }
        }
    }
}