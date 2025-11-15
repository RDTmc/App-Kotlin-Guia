package com.milsabores.appkotlin_guia.ui.screens

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
// import androidx.media3.test.utils.Action
import androidx.navigation.NavController
import com.milsabores.appkotlin_guia.model.CartItem
import com.milsabores.appkotlin_guia.navigation.AppRoute
import com.milsabores.appkotlin_guia.ui.theme.BlancoDos
import com.milsabores.appkotlin_guia.ui.theme.BlancoMarfil
import com.milsabores.appkotlin_guia.ui.theme.Chocolate
import com.milsabores.appkotlin_guia.ui.theme.RosaClaro
import com.milsabores.appkotlin_guia.ui.theme.RosaClaroDos
import com.milsabores.appkotlin_guia.ui.theme.TextoPrincipal
import com.milsabores.appkotlin_guia.ui.util.isCakeLike
import com.milsabores.appkotlin_guia.ui.util.priceFor
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
    catalogVm: CatalogViewModel,
    isLoggedIn: Boolean
) {
    val vm: ProductDetailViewModel = viewModel()
    val ui by vm.ui.collectAsState()
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    // cargar el producto al entrar
    LaunchedEffect(productId) { vm.load(productId, catalogVm) }

    // Snackbar + label temporal del botón
    val snackbarHost = remember { SnackbarHostState() }
    var showAddedLabel by remember { mutableStateOf(false) }
    var lastAddedQty by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = BlancoDos,
        snackbarHost = { SnackbarHost(hostState = snackbarHost) },
        topBar = {
            val cartCount = cartVm.ui.collectAsState().value.items.sumOf { it.quantity }
            TopAppBar(
                title = { Text(ui.product?.nombre ?: "Producto") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Chocolate,
                    navigationIconContentColor = Chocolate,
                    actionIconContentColor = Chocolate
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(AppRoute.Cart.route) }) {
                        BadgedBox(badge = { if (cartCount > 0) Badge { Text("$cartCount") } }) {
                            Icon(Icons.Filled.ShoppingBasket, contentDescription = "Carrito")
                        }
                    }
                }
            )
        },
        bottomBar = {
            // Barra de acciones del producto (sin BottomNavBar aquí)
            BottomAppBar(containerColor = RosaClaroDos) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { vm.decQty() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Chocolate),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(Chocolate)
                        )
                    ) { Text("−") }

                    val alpha by animateFloatAsState(
                        targetValue = if (ui.showShine) 1f else 0f,
                        animationSpec = tween(350, easing = FastOutLinearInEasing),
                        finishedListener = { vm.consumeShine() }
                    )

                    Text(
                        " ${ui.qty} ",
                        modifier = Modifier.padding(horizontal = 8.dp),
                        fontWeight = FontWeight.SemiBold
                    )

                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .alpha(alpha)
                            .background(RosaClaroDos, shape = androidx.compose.material3.MaterialTheme.shapes.small)
                    )

                    OutlinedButton(
                        onClick = { vm.incQty() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Chocolate),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(Chocolate)
                        )
                    ) { Text("+") }

                    Spacer(Modifier.weight(1f))

                    Button(
                        onClick = {
                            val p = ui.product ?: return@Button
                            val unit = priceFor(p, p.precio, ui.selectedTamano)
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
                            // Feedback: label temporal + snackbar con acción
                            lastAddedQty = ui.qty
                            showAddedLabel = true
                            scope.launch {
                                val result = snackbarHost.showSnackbar(
                                    message = "Agregado al carrito",
                                    actionLabel = "Ver carrito",
                                    withDismissAction = true,
                                    duration = SnackbarDuration.Short
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    navController.navigate(AppRoute.Cart.route)
                                }

                                kotlinx.coroutines.delay(1600)
                                showAddedLabel = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Chocolate,
                            contentColor = BlancoMarfil
                        ),
                        modifier = Modifier.height(46.dp)
                    ) {
                        Text(addedLabel("Agregar al carrito", showAddedLabel, lastAddedQty), fontWeight = FontWeight.Bold)
                    }
                }
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
                CircularProgressIndicator(color = Chocolate)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(pv)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Imagen grande con zoom
            val res = resIdFor(ctx, p.imagen ?: "")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                contentAlignment = Alignment.Center
            ) {
                if (res != 0) {
                    Image(
                        painter = painterResource(res),
                        contentDescription = p.nombre,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { vm.setZoom(true) },
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("Sin imagen", color = TextoPrincipal)
                }
            }

            // Título + rating + precio calculado
            Text(p.nombre, fontWeight = FontWeight.Bold)
            Text("★★★★☆ (128 reseñas)")
            val finalPrice = priceFor(p, p.precio, ui.selectedTamano)
            Text("$$finalPrice", fontWeight = FontWeight.ExtraBold, color = Chocolate)

            // Chips de tamaño (porciones o tallas)
            Text(
                if (isCakeLike(p)) "Tamaño (porciones)" else "Tamaño",
                fontWeight = FontWeight.SemiBold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ui.tamanos.forEach { t ->
                    FilterChip(
                        selected = ui.selectedTamano == t,
                        onClick = { vm.setTamano(t) },
                        label = {
                            Text(
                                when {
                                    isCakeLike(p) && t == "10 porciones" -> "10 (+3.000)"
                                    isCakeLike(p) && t == "12 porciones" -> "12 (+5.000)"
                                    !isCakeLike(p) && t == "Mediano" -> "Mediano (+1.500)"
                                    !isCakeLike(p) && t == "Grande" -> "Grande (+3.000)"
                                    else -> t
                                },
                                color = if (ui.selectedTamano == t) RosaClaro else Chocolate
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = RosaClaro,
                            labelColor = Chocolate,
                            selectedContainerColor = Chocolate,
                            selectedLabelColor = RosaClaro
                        )
                    )
                }
            }

        }

        // Modal de zoom
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

/** Texto dinámico para el botón principal. */
private fun addedLabel(default: String, show: Boolean, qty: Int): String =
    if (show) "Agregado ✓ ($qty)" else default
