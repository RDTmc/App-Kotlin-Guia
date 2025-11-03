package com.milsabores.appkotlin_guia.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.milsabores.appkotlin_guia.navigation.AppRoute
import com.milsabores.appkotlin_guia.ui.util.resIdFor
import com.milsabores.appkotlin_guia.viewmodel.CartUiState
import com.milsabores.appkotlin_guia.viewmodel.CartViewModel
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    vm: CartViewModel,
    isGuest: Boolean,
    onLoginRequested: () -> Unit
) {
    val ui by vm.ui.collectAsState()
    val ctx = LocalContext.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Snackbar host para informar al invitado
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Carrito") })
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { pv ->
        Box(
            Modifier
                .padding(pv)
                .fillMaxSize()
        ) {
            if (ui.items.isEmpty()) {
                Box(
                    Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tu carrito está vacío")
                }
            } else {
                Column(
                    Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = ui.items,
                            key = { it.productId to (it.size ?: "") }
                        ) { item ->
                            // recordar la imagen por su nombre
                            val imgRes = remember(item.image) {
                                resIdFor(ctx, item.image ?: "")
                            }

                            val onInc by rememberUpdatedState(newValue = { vm.inc(item.productId) })
                            val onDec by rememberUpdatedState(newValue = { vm.dec(item.productId) })
                            val onRemove by rememberUpdatedState(newValue = { vm.remove(item.productId) })

                            CartItemRow(
                                name = item.name,
                                size = item.size,
                                unitPrice = item.unitPrice,
                                quantity = item.quantity,
                                imageRes = imgRes,
                                onInc = onInc,
                                onDec = onDec,
                                onRemove = onRemove
                            )
                        }
                    }

                    // SummarySection: botón único, activo si hay ítems
                    SummarySection(
                        ui = ui,
                        isGuest = isGuest,
                        onLoginRequested = onLoginRequested,
                        onContinue = {
                            // Si es invitado, mostramos Snackbar con acción para iniciar sesión
                            if (isGuest) {
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Debes iniciar sesión para completar la compra.",
                                        actionLabel = "Iniciar sesión",
                                        duration = SnackbarDuration.Short
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        onLoginRequested()
                                    }
                                }
                            } else {
                                // Usuario autenticado: navegar al checkout
                                navController.navigate(AppRoute.Checkout.route)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(
    name: String,
    size: String?,
    unitPrice: Int,
    quantity: Int,
    imageRes: Int,
    onInc: () -> Unit,
    onDec: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (imageRes != 0) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = name,
                modifier = Modifier
                    .size(64.dp)
                    .padding(end = 8.dp),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .padding(end = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("IMG")
            }
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(name, fontWeight = FontWeight.SemiBold)
            if (size != null) {
                Text("Tamaño: $size", style = MaterialTheme.typography.bodySmall)
            }
            Text("Precio: ${formatCLP(unitPrice)}", style = MaterialTheme.typography.bodySmall)
        }

        // controles más livianos
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            IconButton(
                onClick = onDec,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Menos")
            }
            Text("$quantity", modifier = Modifier.padding(horizontal = 2.dp))
            IconButton(
                onClick = onInc,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Más")
            }
        }

        IconButton(onClick = onRemove) {
            Icon(Icons.Default.Delete, contentDescription = "Quitar", tint = Color(0xFF573123))
        }
    }
}

/**
 * SummarySection compacta: mantiene el botón "Continuar compra" y muestra total.
 * onContinue: acción única que decide comportamiento según contexto (guest o auth).
 */
@Composable
private fun SummarySection(
    ui: CartUiState,
    isGuest: Boolean,
    onLoginRequested: () -> Unit,
    onContinue: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // ojo: si ya tienes un SnackbarHost en el Scaffold, pásale este mismo
    LaunchedEffect(Unit) {
        // nada, sólo para que exista el host
    }

    Surface(tonalElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
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
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF573123),
                    contentColor = Color.White
                )
            ) {
                Text("Continuar compra")
            }
        }
    }
}

private fun formatCLP(value: Int): String =
    "$" + "%,d".format(value).replace(',', '.')