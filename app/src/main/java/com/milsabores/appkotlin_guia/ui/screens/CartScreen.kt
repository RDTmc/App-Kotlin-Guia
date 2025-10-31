package com.milsabores.appkotlin_guia.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.milsabores.appkotlin_guia.model.CartItem
import com.milsabores.appkotlin_guia.navigation.AppRoute
import com.milsabores.appkotlin_guia.ui.util.resIdFor
import com.milsabores.appkotlin_guia.viewmodel.CartViewModel
import androidx.compose.material3.*



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    vm: CartViewModel,
    isGuest: Boolean,
    onLoginRequested: () -> Unit
) {
    val ui by vm.ui.collectAsState()
    var showAuthDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Text(
                text = "Carrito de compras",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(16.dp))

            if (ui.items.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tu carrito está vacío")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(ui.items) { item ->
                        CartItemRow(
                            item = item,
                            onInc = { vm.inc(item.productId) },
                            onDec = { vm.dec(item.productId) },
                            onRemove = { vm.remove(item.productId) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            SummaryBox(
                subtotal = ui.subtotal,
                iva = ui.iva,
                shipping = ui.shipping,
                discount = ui.discount,
                total = ui.total
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (isGuest) {
                        // obligatorio registrarse / loguearse
                        showAuthDialog = true
                    } else {
                        // aquí luego va el Checkout real
                        navController.navigate(AppRoute.Resumen.route)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = ui.items.isNotEmpty()
            ) {
                Text("Continuar compra")
            }
        }

        // Modal de autenticación (solo login/registro)
        if (showAuthDialog) {
            AlertDialog(
                onDismissRequest = { showAuthDialog = false },
                title = { Text("Inicia sesión para continuar") },
                text = {
                    Text("Para finalizar tu compra debes iniciar sesión o crear una cuenta.")
                },
                confirmButton = {
                    TextButton(onClick = {
                        showAuthDialog = false
                        onLoginRequested()
                    }) {
                        Text("Ir a registro / login")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAuthDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItem,
    onInc: () -> Unit,
    onDec: () -> Unit,
    onRemove: () -> Unit
) {
    val ctx = LocalContext.current
    val res = resIdFor(ctx, item.image)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (res != 0) {
            Image(
                painter = painterResource(res),
                contentDescription = item.name,
                modifier = Modifier.size(72.dp),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text("Sin\nimg")
            }
        }

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(item.name, fontWeight = FontWeight.SemiBold)
            if (item.size != null) {
                Text("Tamaño: ${item.size}", style = MaterialTheme.typography.bodySmall)
            }
            if (item.flavor != null) {
                Text("Sabor: ${item.flavor}", style = MaterialTheme.typography.bodySmall)
            }
            Text("c/u $${item.unitPrice}", style = MaterialTheme.typography.bodySmall)
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(onClick = onDec, enabled = item.quantity > 1) {
                    Text("−")
                }
                Text(
                    text = item.quantity.toString(),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                OutlinedButton(onClick = onInc, enabled = item.quantity < 10) {
                    Text("+")
                }
            }
            Spacer(Modifier.height(6.dp))
            Text("Total: $${item.lineTotal}", fontWeight = FontWeight.Bold)
            TextButton(onClick = onRemove) {
                Text("Quitar")
            }
        }
    }
}

@Composable
private fun SummaryBox(
    subtotal: Int,
    iva: Int,
    shipping: Int,
    discount: Int,
    total: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp)
    ) {
        RowItem(label = "Subtotal", value = subtotal)
        RowItem(label = "IVA (19%)", value = iva)
        RowItem(label = "Envío", value = shipping)
        if (discount != 0) {
            RowItem(label = "Descuento", value = -discount)
        }
        Divider(Modifier.padding(vertical = 6.dp))
        RowItem(label = "Total", value = total, bold = true)
    }
}

@Composable
private fun RowItem(label: String, value: Int, bold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(
            "$$value",
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
        )
    }
}