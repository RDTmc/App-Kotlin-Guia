package com.milsabores.appkotlin_guia.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.milsabores.appkotlin_guia.model.CartItem
import com.milsabores.appkotlin_guia.model.Product
import com.milsabores.appkotlin_guia.navigation.AppRoute
import com.milsabores.appkotlin_guia.ui.components.ProductCard
import com.milsabores.appkotlin_guia.ui.util.resIdFor
import com.milsabores.appkotlin_guia.viewmodel.CartViewModel
import com.milsabores.appkotlin_guia.viewmodel.CatalogViewModel
import com.milsabores.appkotlin_guia.viewmodel.ProductDetailViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavController,
    productId: String,
    cartVm: CartViewModel,
    catalogVm: CatalogViewModel
) {
    // Compose provee en esta destination
    val vm: ProductDetailViewModel = viewModel()
    val ui by vm.ui.collectAsState()
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    // cargar el producto al entrar
    LaunchedEffect(productId) {
        vm.load(productId, catalogVm)
    }

    var addedMsg by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(ui.product?.nombre ?: "Producto") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            val cartCount = cartVm.ui.collectAsState().value.items.sumOf { it.quantity }

            Column {
                // 1) barra de acciones del producto (la tuya)
                BottomAppBar(containerColor = Color(0xFFFFF5E1)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(onClick = { vm.decQty() }) { Text("−") }

                        val alpha by animateFloatAsState(
                            targetValue = if (ui.showShine) 1f else 0f,
                            animationSpec = tween(350, easing = FastOutLinearInEasing),
                            finishedListener = { vm.consumeShine() }
                        )

                        Text(
                            " ${ui.qty} ",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .alpha(alpha)
                                .background(Color(0xFFFFC0CB), shape = MaterialTheme.shapes.small)
                        )

                        OutlinedButton(onClick = { vm.incQty() }) { Text("+") }

                        Spacer(Modifier.weight(1f))
                        Button(
                            onClick = {
                                val p = ui.product ?: return@Button
                                val unit = calcPriceWithSize(p.precio, ui.selectedTamano)
                                cartVm.addOrIncrease(
                                    CartItem(
                                        productId = p.id,
                                        name = p.nombre,
                                        image = p.imagen,
                                        size = ui.selectedTamano,
                                        flavor = null,
                                        quantity = ui.qty,
                                        unitPrice = unit
                                    )
                                )
                                addedMsg = true
                                scope.launch {
                                    kotlinx.coroutines.delay(1600)
                                    addedMsg = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF573123),
                                contentColor = Color.White
                            ),
                            modifier = Modifier.height(46.dp)
                        ) {
                            Text("Agregar al carrito")
                        }
                    }
                }

                // 2) bottom nav reutilizada (la misma que en Home)
                com.milsabores.appkotlin_guia.ui.components.BottomNavBar(
                    current = com.milsabores.appkotlin_guia.ui.components.BottomDest.HOME,
                    cartCount = cartCount,
                    onSelect = { dest ->
                        when (dest) {
                            com.milsabores.appkotlin_guia.ui.components.BottomDest.HOME ->
                                navController.navigate(AppRoute.Home.route)
                            com.milsabores.appkotlin_guia.ui.components.BottomDest.MENU ->
                                navController.navigate(AppRoute.Resumen.route)
                            com.milsabores.appkotlin_guia.ui.components.BottomDest.CART ->
                                navController.navigate(AppRoute.Cart.route)
                            com.milsabores.appkotlin_guia.ui.components.BottomDest.PROFILE ->
                                navController.navigate(AppRoute.Profile.route)
                        }
                    }
                )
            }
        }

    ) { pv ->
        val p = ui.product
        if (p == null) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(pv),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(pv)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Imagen grande
            val res = resIdFor(ctx, p.imagen ?: "")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(Color(0xFFF2F2F2)),
                contentAlignment = Alignment.Center
            ) {
                if (res != 0) {
                    Image(
                        painter = painterResource(res),
                        contentDescription = p.nombre,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { vm.setZoom(true) },
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("Sin imagen")
                }
            }

            // mensaje de agregado
            AnimatedVisibility(
                visible = addedMsg,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp)
            ) {
                Surface(
                    color = Color(0xAA000000),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Check,
                            contentDescription = null, tint = Color.White)
                        Spacer(Modifier.width(6.dp))
                        Text("Agregado al carrito", color = Color.White)
                    }
                }
            }


            // título + precio + rating fijo
            Text(p.nombre, style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold)
            Text("★★★★☆ (128 reseñas)")
            // precio mostrado con recargo
            val finalPrice = calcPriceWithSize(p.precio, ui.selectedTamano)
            Text(
                "$$finalPrice",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF573123)
            )

            // Tamaños (con recargo)
            Text("Tamaño / porciones", fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ui.tamanos.forEach { t ->
                    FilterChip(
                        selected = ui.selectedTamano == t,
                        onClick = { vm.setTamano(t) },
                        label = {
                            Text(
                                when (t) {
                                    "10 porciones" -> "10 porciones"
                                    "12 porciones" -> "12 porciones"
                                    else -> t
                                }
                            )
                        }
                    )
                }
            }

            // ir al carrito
            OutlinedButton(
                onClick = { navController.navigate(AppRoute.Cart.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ir al carrito")
            }
        }

        // modal zoom
        if (ui.showZoom && ui.product != null) {
            val zoomRes = resIdFor(ctx, ui.product!!.imagen ?: "")

            AlertDialog(
                onDismissRequest = { vm.setZoom(false) },
                confirmButton = {},
                text = {
                    if (zoomRes != 0) {
                        Image(
                            painter = painterResource(zoomRes),
                            contentDescription = ui.product!!.nombre,
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            )
        }
    }
}

/**
 * Calcula precio según tamaño:
 * 8 porciones → base
 * 10 porciones → +3.000
 * 12 porciones → +5.000
 */
private fun calcPriceWithSize(base: Int, size: String?): Int =
    when (size) {
        "10 porciones" -> base + 3000
        "12 porciones" -> base + 5000
        else -> base
    }