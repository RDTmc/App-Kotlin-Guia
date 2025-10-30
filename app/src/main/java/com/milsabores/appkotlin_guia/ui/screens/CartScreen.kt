package com.milsabores.appkotlin_guia.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.milsabores.appkotlin_guia.model.CartItem
import com.milsabores.appkotlin_guia.navigation.AppRoute
import com.milsabores.appkotlin_guia.ui.components.AuthRequiredDialog
import com.milsabores.appkotlin_guia.ui.util.resIdFor
import com.milsabores.appkotlin_guia.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    vm: CartViewModel = viewModel()
) {
    val ui by vm.ui.collectAsState()
    var showAuthDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrito de compras") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { pv ->
        Column(
            modifier = Modifier
                .padding(pv)
                .fillMaxSize()
        ) {
            if (ui.items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No tienes productos en el carrito")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(ui.items) { item ->
                        CartItemRow(
                            item = item,
                            onInc = { vm.updateQty(item.productId, +1) },
                            onDec = { vm.updateQty(item.productId, -1) },
                            onRemove = { vm.remove(item.productId) }
                        )
                    }
                }

                // Resumen
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal")
                            Text("$${ui.subtotal}")
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("IVA (19%)")
                            Text("$${ui.iva}")
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Envío")
                            Text(if (ui.shipping == 0) "—" else "$${ui.shipping}")
                        }
                        if (ui.discount > 0) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Descuento")
                                Text("-$${ui.discount}")
                            }
                        }
                        Divider()
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total", fontWeight = FontWeight.Bold)
                            Text("$${ui.total}", fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = {
                                // aquí entra la lógica:
                                // si no está logueado → mostrar modal
                                showAuthDialog = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            enabled = ui.items.isNotEmpty()
                        ) {
                            Text("Continuar compra")
                        }
                    }
                }
            }
        }

        if (showAuthDialog) {
            AuthRequiredDialog(
                onDismiss = { showAuthDialog = false },
                onLogin = { navController.navigate(AppRoute.Register.route) },    // o AppRoute.Login si lo tienes
                onRegister = { navController.navigate(AppRoute.Register.route) },
                onGuest = {
                    // permitir checkout invitado → navegar a Checkout
                    navController.navigate(AppRoute.Resumen.route)
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
    val res = item.image?.let { resIdFor(ctx, it) } ?: 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8F8F8))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            if (res != 0) {
                Image(
                    painter = painterResource(res),
                    contentDescription = item.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text("IMG")
            }
        }

        Spacer(Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, fontWeight = FontWeight.SemiBold)
            if (item.size != null || item.flavor != null) {
                Text(
                    listOfNotNull(item.size, item.flavor).joinToString(" • "),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text("Unit: $${item.unitPrice}", style = MaterialTheme.typography.bodySmall)
        }

        // qty
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(onClick = onDec, enabled = item.quantity > 1) {
                Text("−")
            }
            Text("${item.quantity}", fontWeight = FontWeight.Bold)
            IconButton(onClick = onInc, enabled = item.quantity < 10) {
                Text("+")
            }
        }

        IconButton(onClick = onRemove) {
            Icon(Icons.Default.Delete, contentDescription = "Quitar")
        }
    }
}